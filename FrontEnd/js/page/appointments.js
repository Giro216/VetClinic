import { getAppointments, getPets, getAvailableSlots, bookAppointment } from '../app.js';

// Глобальные переменные для хранения выбранных данных
let selectedSlot = null;
let selectedDoctorId = null;

document.addEventListener("DOMContentLoaded", async () => {
    const appointments = await getAppointments();
    renderAppointments(appointments);
    initAppointmentForm();
});

function initAppointmentForm() {
    const doctorSelectContainer = document.getElementById('doctorSelectContainer');
    const doctorSelect = document.getElementById('doctorSelect');
    const petSelect = document.getElementById('petSelect');
    const confirmBtn = document.getElementById('confirmAppointmentBtn');
    const slotsContainer = document.getElementById('slotsContainer');
    const appointmentDate = document.getElementById('appointmentDate');
    
    // Загрузка питомцев
    getPets().then(pets => {
        petSelect.innerHTML = pets.map(pet => 
            `<option value="${pet.petId}">${pet.name} (${pet.kind})</option>`
        ).join('');
    });
    
    // При выборе даты показываем выбор врача
    appointmentDate.addEventListener('change', function() {
        if (this.value) {
            // Сбрасываем выбранного врача
            doctorSelect.value = '';
            doctorSelect.disabled = false;
            doctorSelectContainer.style.display = 'block';
            
            // Сбрасываем слоты
            slotsContainer.style.display = 'none';
            document.getElementById('slotsGrid').innerHTML = '';
            
            // Деактивируем кнопку подтверждения
            confirmBtn.disabled = true;
            selectedSlot = null;
            selectedDoctorId = null;
        } else {
            doctorSelectContainer.style.display = 'none';
            slotsContainer.style.display = 'none';
        }
    });
    
    // При выборе врача загружаем слоты
    doctorSelect.addEventListener('change', async function() {
        // Сбрасываем предыдущий выбор
        document.getElementById('slotsGrid').innerHTML = '';
        selectedSlot = null;
        confirmBtn.disabled = true;
        
        selectedDoctorId = this.value;
        if (!selectedDoctorId || !appointmentDate.value) return;
        
        slotsContainer.style.display = 'block';
        const slots = await getAvailableSlots(selectedDoctorId, appointmentDate.value);
        renderSlots(slots);
    });
    
    // Обработчик кнопки подтверждения
    confirmBtn.addEventListener('click', async () => {
        const petId = document.getElementById('petSelect').value;
        const reason = document.getElementById('appointmentReason').value;
        
        if (!petId || !reason || !selectedSlot || !selectedDoctorId) {
            alert('Пожалуйста, заполните все поля и выберите время');
            return;
        }
        
        try {
            await bookAppointment(selectedSlot.id, selectedDoctorId, petId, reason);
            alert('Запись успешно создана!');
            
            // Закрываем модальное окно
            bootstrap.Modal.getInstance(document.getElementById('appointmentModal')).hide();
            
            // Обновляем список записей
            const appointments = await getAppointments();
            renderAppointments(appointments);
            
            // Сбрасываем форму
            resetForm();
        } catch (error) {
            console.error('Booking error:', error);
            alert('Ошибка при создании записи');
        }
    });
}

function renderSlots(slots) {
    const container = document.getElementById('slotsGrid');
    container.innerHTML = '';
    selectedSlot = null;
    document.getElementById('confirmAppointmentBtn').disabled = true;
    
    if (slots.length === 0) {
        container.innerHTML = '<div class="alert alert-info">Нет доступных слотов на выбранную дату</div>';
        return;
    }
    
    slots.forEach(slot => {
        const start = new Date(slot.startTime);
        const slotElement = document.createElement('button');
        slotElement.type = 'button';
        slotElement.className = 'btn btn-outline-primary slot-btn mb-2';
        slotElement.innerHTML = start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        
        slotElement.addEventListener('click', () => {
            // Убираем выделение у всех слотов
            document.querySelectorAll('.slot-btn').forEach(btn => {
                btn.classList.remove('btn-primary');
                btn.classList.add('btn-outline-primary');
            });
            
            // Выделяем выбранный слот
            slotElement.classList.remove('btn-outline-primary');
            slotElement.classList.add('btn-primary');
            
            // Сохраняем выбранный слот
            selectedSlot = slot;
            
            // Активируем кнопку подтверждения
            document.getElementById('confirmAppointmentBtn').disabled = false;
        });
        
        container.appendChild(slotElement);
    });
}

function resetForm() {
    document.getElementById('appointmentForm').reset();
    document.getElementById('slotsGrid').innerHTML = '';
    selectedSlot = null;
    selectedDoctorId = null;
    document.getElementById('confirmAppointmentBtn').disabled = true;
}

function renderAppointments(appointments) {
    const container = document.getElementById('appointmentsList');
    container.innerHTML = appointments.map(app => `
        <div class="col-md-6 mb-4">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">${app.petName || 'Питомец'}</h5>
                    <div class="card-text">
                        <strong>Дата:</strong> ${new Date(app.startTime).toLocaleString()}<br>
                        <strong>Статус:</strong> <span class="badge bg-${app.status === 'CONFIRMED' ? 'success' : 'warning'}">${app.status}</span><br>
                        <strong>Причина:</strong> ${app.reason || 'Не указана'}
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}