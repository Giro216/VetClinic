import { getAppointments, getDoctors, getAvailableSlotsByDate, bookAppointment } from '../api/appointment_api.js';
import { getPets } from '../api/Pets_api.js';

let selectedSlot = null;
let selectedDoctorIdForBooking = null;
let allAvailableSlotsForDate = [];

const petSelect = document.getElementById('petSelect');
const confirmBtn = document.getElementById('confirmAppointmentBtn');
const slotsContainer = document.getElementById('slotsContainer');
const slotsGrid = document.getElementById('slotsGrid');
const appointmentDate = document.getElementById('appointmentDate');
const appointmentModalElement = document.getElementById('appointmentModal');
const appointmentForm = document.getElementById('appointmentForm');
const appointmentReason = document.getElementById('appointmentReason');

const doctorSelectContainer = document.getElementById('doctorSelectContainer');
const doctorSelect = document.getElementById('doctorSelect');


document.addEventListener("DOMContentLoaded", async () => {
    await loadAndRenderAppointments();
    await initAppointmentForm();
    setupFormEventListeners();

    confirmBtn.addEventListener('click', handleBookingAttempt);
});

async function loadAndRenderAppointments() {
     try {
        const appointments = await getAppointments();
        renderAppointments(appointments);
     } catch (error) {
        console.error("Ошибка загрузки записей:", error);
        const container = document.getElementById('appointmentsList');
         if (container) {
             container.innerHTML = `
                 <div class="col-12">
                     <div class="alert alert-danger">Не удалось загрузить список записей: ${error.message}</div>
                 </div>
             `;
         }
     }
}

async function initAppointmentForm() {
    const today = new Date();
    const todayISO = today.toISOString().split('T')[0];
    appointmentDate.min = todayISO;

    appointmentDate.disabled = false

    await loadPetsForForm();

    await loadDoctorsForForm();

    doctorSelectContainer.style.display = 'none';
    doctorSelect.disabled = true;

    slotsContainer.style.display = 'none';
    confirmBtn.disabled = true;

     checkFormCompleteness();
}

async function loadPetsForForm() {
    try {
        const pets = await getPets();
        petSelect.innerHTML = `<option value="" disabled selected>Выберите питомца</option>`;
        if (pets && pets.length > 0) {
             petSelect.innerHTML += pets.map(pet =>
                 `<option value="${pet.petId}">${pet.name} (${pet.kind})</option>`
             ).join('');
        } else {
            petSelect.innerHTML = `<option value="" disabled selected>Добавьте питомца, чтобы записаться</option>`;
            appointmentDate.disabled = true;
            doctorSelect.disabled = true;
            confirmBtn.disabled = true;
             if(petSelect.options.length > 0) petSelect.options[0].disabled = true;
        }
    } catch (error) {
        petSelect.innerHTML = `<option value="" disabled selected>Ошибка загрузки питомцев</option>`;
        appointmentDate.disabled = true;
        doctorSelect.disabled = true;
        confirmBtn.disabled = true;
        if(petSelect.options.length > 0) petSelect.options[0].disabled = true;
    }
}

async function loadDoctorsForForm() {
    try {
        const doctors = await getDoctors();
        doctorSelect.innerHTML = `<option value="" disabled selected>Выберите врача</option>`;
        if (doctors && doctors.length > 0) {
             doctorSelect.innerHTML += doctors.map(doctor =>
                 `<option value="${doctor.id}">${doctor.name}</option>`
             ).join('');
        } else {
             doctorSelect.innerHTML = `<option value="" disabled selected>Врачи недоступны</option>`;
        }
         checkFormCompleteness();
    } catch (error) {
        doctorSelect.innerHTML = `<option value="" disabled selected>Ошибка загрузки врачей</option>`;
        doctorSelect.disabled = true;
        checkFormCompleteness();
    }
}

function setupFormEventListeners() {
    appointmentDate.addEventListener('change', async function() {
        resetSlotSelection();
        selectedDoctorIdForBooking = null;

        slotsGrid.innerHTML = '';

        const selectedDate = this.value;

        if (!selectedDate) {
             slotsContainer.style.display = 'none';
             checkFormCompleteness();
             return;
        }

        slotsContainer.style.display = 'block';
        slotsGrid.innerHTML = '<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Загрузка...</span></div></div>';

        try {
            allAvailableSlotsForDate = await getAvailableSlotsByDate(selectedDate);
            renderSlots(allAvailableSlotsForDate);
        } catch (error) {
            slotsGrid.innerHTML = `<div class="alert alert-danger">Не удалось загрузить доступные слоты на выбранную дату: ${error.message}</div>`;
        }
        checkFormCompleteness();
    });

    slotsGrid.addEventListener('click', function(event) {
        const target = event.target;
        if (target.classList.contains('slot-btn')) {
            const clickedSlotId = target.getAttribute('data-slot-id');
            if (!clickedSlotId) return;

            const slotsData = slotsGrid._filteredSlotsData;
            const clickedSlot = slotsData ? slotsData.find(s => s.id == clickedSlotId) : null;

            if (clickedSlot) {
                if (selectedSlot && selectedSlot.id == clickedSlot.id) {
                    resetSlotSelection();
                     selectedDoctorIdForBooking = null;
                } else {
                    document.querySelectorAll('.slot-btn').forEach(btn => {
                        btn.classList.remove('btn-primary');
                        btn.classList.add('btn-outline-primary');
                    });
                    target.classList.remove('btn-outline-primary');
                    target.classList.add('btn-primary');
                    selectedSlot = clickedSlot;

                    selectedDoctorIdForBooking = null;
                    if (selectedSlot.availabilities && Array.isArray(selectedSlot.availabilities)) {
                        const availableAvailability = selectedSlot.availabilities.find(av => av.available === true && av.doctor && av.doctor.id);
                        if (availableAvailability) {
                            selectedDoctorIdForBooking = availableAvailability.doctor.id;
                            console.log("Выбран слот:", selectedSlot.id, "Автоматически выбран врач для бронирования:", selectedDoctorIdForBooking);
                        } else {
                             console.error("В выбранном слоте", selectedSlot.id, "нет доступных врачей согласно availabilities.");
                        }
                    } else {
                         console.error("В выбранном слоте", selectedSlot.id, "отсутствуют или некорректны данные availabilities.");
                    }

                     checkFormCompleteness();
                }
            } else {
                console.error("Данные выбранного слота не найдены:", clickedSlotId);
                resetSlotSelection();
                 selectedDoctorIdForBooking = null;
            }
        }
    });

    appointmentModalElement.addEventListener('hidden.bs.modal', function () {
        resetAppointmentForm();
    });

     petSelect.addEventListener('change', checkFormCompleteness);
     appointmentReason.addEventListener('input', checkFormCompleteness);
}

async function handleBookingAttempt() {
    const petId = petSelect.value;
    const slotId = selectedSlot ? selectedSlot.id : null;
    const doctorId = selectedDoctorIdForBooking;
    const reason = appointmentReason.value.trim();

    if (!petId || !slotId || !doctorId || !reason) {
        console.error("Не все данные для бронирования выбраны:", {petId, slotId, doctorId, reason});
        return;
    }

    confirmBtn.disabled = true;
    confirmBtn.textContent = 'Бронирование...';

    try {
        const bookingResult = await bookAppointment(slotId, doctorId, petId, reason);
        console.log("Результат бронирования:", bookingResult);

        alert("Запись успешно создана!");

        const modalInstance = bootstrap.Modal.getInstance(appointmentModalElement);
        if (modalInstance) {
            modalInstance.hide();
        }

        await loadAndRenderAppointments();

    } catch (error) {
        console.error("Ошибка при бронировании записи:", error);
        alert(`Не удалось создать запись: ${error.message || error}`);

    } finally {
        confirmBtn.disabled = false;
        confirmBtn.textContent = 'Подтвердить запись';
        checkFormCompleteness();
    }
}

function renderSlots(slots) {
    const container = slotsGrid;
    container.innerHTML = '';
    selectedSlot = null;
    selectedDoctorIdForBooking = null;

    container._filteredSlotsData = slots;

    if (!slots || slots.length === 0) {
        container.innerHTML = '<div class="alert alert-info">Нет доступных слотов на выбранную дату.</div>';
        return;
    }

    slots.forEach(slot => {
        const startTime = new Date(slot.startTime);
        const slotElement = document.createElement('button');
        slotElement.type = 'button';
        slotElement.className = 'btn btn-outline-primary slot-btn mb-2 me-2';
        slotElement.setAttribute('data-slot-id', slot.id);
        slotElement.textContent = startTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

        container.appendChild(slotElement);
    });
    checkFormCompleteness();
}

function checkFormCompleteness() {
     const petId = petSelect.value;
     const reason = appointmentReason.value.trim();

     if (petId && selectedSlot && selectedDoctorIdForBooking && reason) {
         confirmBtn.disabled = false;
     } else {
         confirmBtn.disabled = true;
     }
}

function resetSlotSelection() {
     document.querySelectorAll('.slot-btn').forEach(btn => {
        btn.classList.remove('btn-primary');
        btn.classList.add('btn-outline-primary');
     });
     selectedSlot = null;
     selectedDoctorIdForBooking = null;
     checkFormCompleteness();
}

function resetDoctorAndSlotSelection() {
    resetSlotSelection();
    if (slotsGrid) {
         slotsGrid._filteredSlotsData = [];
    }
    allAvailableSlotsForDate = [];
}

function resetAppointmentForm() {
    appointmentForm.reset();
    resetDoctorAndSlotSelection();
    slotsGrid.innerHTML = '';
    slotsContainer.style.display = 'none';
    selectedDoctorIdForBooking = null;

    checkFormCompleteness();
}

function renderAppointments(appointments) {
    const container = document.getElementById('appointmentsList');
    container.innerHTML = '';
    if (!appointments || appointments.length === 0) {
         container.innerHTML = `<div class="col-12"><p>У вас пока нет предстоящих записей.</p></div>`;
         return;
    }

    appointments.forEach(app => {
        const petId = app.petId != null ? app.petId.toString() : 'Не указан';
        const bookedDoctorAvailability = app.slot?.availabilities?.find(av => av.doctor?.id === app.doctorId);
        const doctorName = bookedDoctorAvailability?.doctor?.name || 'Не указан';

        const datetimeString = app.slot?.startTime ? new Date(app.slot.startTime).toLocaleString('ru-RU') : 'Дата/время не указаны';

        const status = app.status || 'Неизвестно';
        const reason = app.reason || 'Не указана';
        const statusClass = status === 'BOOKED' ? 'success' : (status === 'COMPLETED' ? 'primary' : 'warning');

        const card = `
            <div class="col-md-6 mb-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Питомец ID: ${petId}</h5>
                        <div class="card-text">
                            <strong>Врач:</strong> ${doctorName}<br>
                            <strong>Дата и время:</strong> ${datetimeString}<br>
                            <strong>Статус:</strong> <span class="badge bg-${statusClass}">${status}</span><br>
                            <strong>Причина:</strong> ${reason}
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += card;
    });
}