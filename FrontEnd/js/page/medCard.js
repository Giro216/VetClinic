import { getMedicalCard, updateMedicalCard } from "../api/Pets_api.js";

let currentPetId = null;
let originalMedicalCard = null;

const pageTitle = document.getElementById('pageTitle');
const loadingIndicator = document.getElementById('loadingIndicator');
const cardDataContainer = document.getElementById('cardData');
const viewModeContent = document.getElementById('viewModeContent');
const editModeContent = document.getElementById('editModeContent');
const editToggleBtn = document.getElementById('editToggleBtn');
const saveChangesBtn = document.getElementById('saveChangesBtn');
const cancelEditBtn = document.getElementById('cancelEditBtn');
const backBtn = document.getElementById('backBtn');
const alertPlaceholder = document.getElementById('alertPlaceholder');

document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    currentPetId = urlParams.get('petId');

    if (!currentPetId) {
        showError("ID питомца не найден в URL.");
        loadingIndicator.style.display = 'none';
        return;
    }

    loadMedicalCard(currentPetId);
    setupEventListeners();
});

async function loadMedicalCard(petId) {
    showLoading(true);
    clearAlert();
    try {
        const medicalCard = await getMedicalCard(petId);
        if (!medicalCard) {
             originalMedicalCard = { vaccinations: [], allergies: [], diseases: [] };
             renderMedicalCard(originalMedicalCard);
             pageTitle.textContent = `Медицинская карта (ID: ${petId}) - Запись не найдена, можно создать`;
        } else {
             originalMedicalCard = JSON.parse(JSON.stringify(medicalCard));
             originalMedicalCard.vaccinations = originalMedicalCard.vaccinations || [];
             originalMedicalCard.allergies = originalMedicalCard.allergies || [];
             originalMedicalCard.diseases = originalMedicalCard.diseases || [];
             renderMedicalCard(originalMedicalCard);
             pageTitle.textContent = `Медицинская карта (ID: ${petId})`;
        }
        showLoading(false);
        switchToViewMode();
    } catch (error) {
        console.error('Ошибка загрузки медкарты:', error);
        showError(`Не удалось загрузить медкарту: ${error.message}`);
        showLoading(false);
    }
}

function renderMedicalCard(medicalCard) {
    renderViewMode(medicalCard);
    renderEditMode(medicalCard);
}

function renderViewMode(medicalCard) {
    viewModeContent.innerHTML = `
        <h6>Прививки:</h6>
        ${(medicalCard.vaccinations && medicalCard.vaccinations.length > 0) ? `
            <ul class="list-group mb-3">
                ${medicalCard.vaccinations.map(vacc => `
                    <li class="list-group-item">
                        <strong>${vacc.type || 'Тип не указан'}</strong> - ${vacc.date || 'Дата не указана'}
                    </li>
                `).join('')}
            </ul>` : '<p class="text-muted">Нет данных о прививках.</p>'}

        <h6>Аллергии:</h6>
        <div class="mb-3">
            ${(medicalCard.allergies && medicalCard.allergies.length > 0 && medicalCard.allergies.some(a => a.trim() !== ''))
                ? medicalCard.allergies.filter(a => a.trim() !== '').join(', ')
                : '<p class="text-muted">Нет данных об аллергиях.</p>'}
        </div>

        <h6>Хронические заболевания:</h6>
        <div>
             ${(medicalCard.diseases && medicalCard.diseases.length > 0 && medicalCard.diseases.some(d => d.trim() !== ''))
                ? medicalCard.diseases.filter(d => d.trim() !== '').join(', ')
                : '<p class="text-muted">Нет данных о хронических заболеваниях.</p>'}
        </div>
    `;
}

function renderEditMode(medicalCard) {
    editModeContent.innerHTML = `
        <h6>Прививки:</h6>
        <div class="mb-3">
            <div id="vaccinationsEditContainer">
                ${(medicalCard.vaccinations || []).map((vacc, index) => renderVaccinationInput(vacc, index)).join('')}
            </div>
            <button type="button" class="btn btn-sm btn-success mb-2" id="addVaccinationBtn">+ Добавить прививку</button>
        </div>

        <h6>Аллергии:</h6>
        <div class="mb-3">
            <textarea class="form-control" id="allergiesEdit" rows="2">${(medicalCard.allergies || []).join(', ')}</textarea>
            <small class="text-muted">Перечислите через запятую</small>
        </div>

        <h6>Хронические заболевания:</h6>
        <div class="mb-3">
            <textarea class="form-control" id="diseasesEdit" rows="2">${(medicalCard.diseases || []).join(', ')}</textarea>
            <small class="text-muted">Перечислите через запятую</small>
        </div>
    `;
    setupEditModeListeners();
}

function renderVaccinationInput(vacc = { type: '', date: '' }, index) {
    return `
        <div class="input-group mb-2 vaccination-item" data-index="${index}">
            <input type="text" class="form-control vaccination-type" value="${vacc.type || ''}" placeholder="Тип прививки">
            <input type="date" class="form-control vaccination-date" value="${vacc.date || ''}">
            <button class="btn btn-outline-danger remove-vaccination" type="button">×</button>
        </div>
    `;
}

function showLoading(isLoading) {
    if (isLoading) {
        loadingIndicator.style.display = 'block';
        cardDataContainer.style.display = 'none';
    } else {
        loadingIndicator.style.display = 'none';
        cardDataContainer.style.display = 'block';
    }
}

function setSaveButtonLoading(isLoading) {
    const spinner = saveChangesBtn.querySelector('.spinner-border');
    if (isLoading) {
        saveChangesBtn.disabled = true;
        spinner.style.display = 'inline-block';
        saveChangesBtn.childNodes[saveChangesBtn.childNodes.length - 1].nodeValue = ' Сохранение...';
    } else {
        saveChangesBtn.disabled = false;
        spinner.style.display = 'none';
         saveChangesBtn.childNodes[saveChangesBtn.childNodes.length - 1].nodeValue = ' Сохранить'; 
    }
}

function switchToViewMode() {
    viewModeContent.style.display = 'block';
    editModeContent.style.display = 'none';
    editToggleBtn.style.display = 'inline-block';
    saveChangesBtn.style.display = 'none';
    cancelEditBtn.style.display = 'none';
    backBtn.style.display = 'inline-block';
}

function switchToEditMode() {
    viewModeContent.style.display = 'none';
    editModeContent.style.display = 'block';
    editToggleBtn.style.display = 'none';
    saveChangesBtn.style.display = 'inline-block';
    cancelEditBtn.style.display = 'inline-block';
     backBtn.style.display = 'none';
}

function setupEventListeners() {
    editToggleBtn.addEventListener('click', () => {
         renderEditMode(originalMedicalCard);
         switchToEditMode();
    });

    saveChangesBtn.addEventListener('click', handleSaveChanges);

    cancelEditBtn.addEventListener('click', () => {
        renderViewMode(originalMedicalCard);
        switchToViewMode();
        clearAlert();
    });

    backBtn.addEventListener('click', () => {
        window.history.back(); 
    });
}

function setupEditModeListeners() {
    document.getElementById('addVaccinationBtn')?.addEventListener('click', () => {
        const container = document.getElementById('vaccinationsEditContainer');
        const newIndex = container.children.length; // Simple index
        const newItemHtml = renderVaccinationInput({ type: '', date: '' }, newIndex);
        container.insertAdjacentHTML('beforeend', newItemHtml);

        const newItemElement = container.lastElementChild;
        newItemElement.querySelector('.remove-vaccination').addEventListener('click', function() {
            newItemElement.remove();
        });
    });

    document.getElementById('vaccinationsEditContainer').addEventListener('click', function(event) {
        if (event.target.classList.contains('remove-vaccination')) {
            event.target.closest('.vaccination-item').remove();
        }
    });
}


async function handleSaveChanges() {
    setSaveButtonLoading(true);
    clearAlert();

    try {
        const updatedMedicalCard = {
            vaccinations: Array.from(document.querySelectorAll('#vaccinationsEditContainer .vaccination-item')).map(item => ({
                type: item.querySelector('.vaccination-type').value.trim(),
                date: item.querySelector('.vaccination-date').value
            })).filter(v => v.type || v.date), 
            allergies: document.getElementById('allergiesEdit').value.split(',')
                         .map(item => item.trim()).filter(item => item !== ''),
            diseases: document.getElementById('diseasesEdit').value.split(',')
                        .map(item => item.trim()).filter(item => item !== '')
        };

        await updateMedicalCard(currentPetId, updatedMedicalCard);

        originalMedicalCard = JSON.parse(JSON.stringify(updatedMedicalCard)); 
        renderViewMode(originalMedicalCard); 
        switchToViewMode();
        showAlert('Изменения успешно сохранены!', 'success');

    } catch (error) {
        console.error('Ошибка при сохранении медкарты:', error);
        showAlert(`Ошибка при сохранении: ${error.message}`, 'danger');
    } finally {
        setSaveButtonLoading(false);
    }
}

function showAlert(message, type = 'danger') {
    const wrapper = document.createElement('div');
    wrapper.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    alertPlaceholder.innerHTML = ''; 
    alertPlaceholder.append(wrapper);
}

function clearAlert() {
    alertPlaceholder.innerHTML = '';
}