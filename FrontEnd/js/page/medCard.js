import { getMedicalCard, updateMedicalCard } from "../api/Pets_api.js";
import { getFoodRecommendations, getCareTips } from "../api/recommendation_api.js";

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

const foodRecommendationsList = document.getElementById('foodRecommendationsList');
const careTipsList = document.getElementById('careTipsList');

document.addEventListener("DOMContentLoaded", async () => {
    const urlParams = new URLSearchParams(window.location.search);
    currentPetId = urlParams.get('petId');

    if (!currentPetId) {
        showError("ID питомца не найден в URL.");
        showLoading(false);
        return;
    }

    await loadMedicalCard(currentPetId);
    await loadRecommendations(currentPetId);
    setupEventListeners();
});

async function loadMedicalCard(petId) {
    showLoading(true);
    clearAlert();
    try {
        const medicalCard = await getMedicalCard(petId);
        if (!medicalCard) {
             originalMedicalCard = { vaccinations: [], allergies: [], diseases: [] };
             pageTitle.textContent = `Медицинская карта (ID: ${petId}) - Запись не найдена`;
        } else {
             originalMedicalCard = JSON.parse(JSON.stringify(medicalCard));
             originalMedicalCard.vaccinations = originalMedicalCard.vaccinations || [];
             originalMedicalCard.allergies = originalMedicalCard.allergies || [];
             originalMedicalCard.diseases = originalMedicalCard.diseases || [];
             pageTitle.textContent = `Медицинская карта (ID: ${petId})`;
        }
        renderMedicalCard(originalMedicalCard);

    } catch (error) {
        console.error('Ошибка загрузки основной медкарты:', error);
        showError(`Не удалось загрузить основную медкарту: ${error.message}`);
    }
}

async function loadRecommendations(petId) {
    try {
        const [foodRecommendations, careTips] = await Promise.all([
            getFoodRecommendations(petId),
            getCareTips(petId)
        ]);

        renderFoodRecommendations(foodRecommendations);
        renderCareTips(careTips);

    } catch (error) {
        console.error('Ошибка загрузки рекомендаций:', error);
        if (foodRecommendationsList) foodRecommendationsList.innerHTML = '<p class="text-danger">Не удалось загрузить рекомендации по корму.</p>';
        if (careTipsList) careTipsList.innerHTML = '<p class="text-danger">Не удалось загрузить советы по уходу.</p>';
    } finally {
        showLoading(false);
        switchToViewMode();
    }
}

function renderMedicalCard(medicalCard) {
    renderViewMode(medicalCard);
    renderEditMode(medicalCard);
}

function renderViewMode(medicalCard) {
    let vaccinationsViewContainer = viewModeContent.querySelector('#vaccinationsViewContainer');
    if (!vaccinationsViewContainer) {
        vaccinationsViewContainer = document.createElement('div');
        vaccinationsViewContainer.id = 'vaccinationsViewContainer';
        const foodSection = viewModeContent.querySelector('#foodRecommendationsSection');
        if (foodSection) {
             foodSection.parentNode.insertBefore(vaccinationsViewContainer, foodSection);
        } else {
             viewModeContent.appendChild(vaccinationsViewContainer);
        }
         vaccinationsViewContainer.insertAdjacentHTML('beforebegin', '<h6>Прививки:</h6>');
    }

    let allergiesViewContainer = viewModeContent.querySelector('#allergiesViewContainer');
    if (!allergiesViewContainer) {
        allergiesViewContainer = document.createElement('div');
        allergiesViewContainer.id = 'allergiesViewContainer';
         vaccinationsViewContainer.insertAdjacentElement('afterend', allergiesViewContainer);
         allergiesViewContainer.insertAdjacentHTML('beforebegin', '<h6>Аллергии:</h6>');
    }

     let diseasesViewContainer = viewModeContent.querySelector('#diseasesViewContainer');
     if (!diseasesViewContainer) {
         diseasesViewContainer = document.createElement('div');
         diseasesViewContainer.id = 'diseasesViewContainer';
          allergiesViewContainer.insertAdjacentElement('afterend', diseasesViewContainer);
          diseasesViewContainer.insertAdjacentHTML('beforebegin', '<h6>Хронические заболевания:</h6>');
     }

    vaccinationsViewContainer.innerHTML = (medicalCard.vaccinations && medicalCard.vaccinations.length > 0) ? `
        <ul class="list-group mb-3">
            ${medicalCard.vaccinations.map(vacc => `
                <li class="list-group-item">
                    <strong>${vacc.type || 'Тип не указан'}</strong> - ${vacc.date || 'Дата не указана'}
                </li>
            `).join('')}
        </ul>` : '<p class="text-muted">Нет данных о прививках.</p>';

    allergiesViewContainer.innerHTML = `
        <div class="mb-3">
            ${(medicalCard.allergies && medicalCard.allergies.length > 0 && medicalCard.allergies.some(a => a.trim() !== ''))
                ? medicalCard.allergies.filter(a => a.trim() !== '').join(', ')
                : '<p class="text-muted">Нет данных об аллергиях.</p>'}
        </div>
    `;

     diseasesViewContainer.innerHTML = `
         <div>
              ${(medicalCard.diseases && medicalCard.diseases.length > 0 && medicalCard.diseases.some(d => d.trim() !== ''))
                 ? medicalCard.diseases.filter(d => d.trim() !== '').join(', ')
                 : '<p class="text-muted">Нет данных о хронических заболеваниях.</p>'}
         </div>
     `;

     const hrSeparator = viewModeContent.querySelector('#separatorAfterBasicCard');
     if (!hrSeparator) {
        const hr = document.createElement('hr');
        hr.id = 'separatorAfterBasicCard';
        const foodSection = viewModeContent.querySelector('#foodRecommendationsSection');
        if (foodSection && foodSection.parentNode) {
             foodSection.parentNode.insertBefore(hr, foodSection);
        } else if (diseasesViewContainer) {
             diseasesViewContainer.insertAdjacentElement('afterend', hr);
        }
     }
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


function renderFoodRecommendations(recommendations) {
     if (!foodRecommendationsList) return;

     if (!recommendations || recommendations.length === 0) {
         foodRecommendationsList.innerHTML = '<p class="text-muted">Нет рекомендаций по корму.</p>';
         return;
     }

     foodRecommendationsList.innerHTML = `
         <ul class="list-group">
             ${recommendations.map(rec => `
                 <li class="list-group-item">
                     <strong>${rec.name || 'Не указан'}</strong> (${rec.brand || 'Бренд не указан'})
                     ${rec.description ? `<br><small class="text-muted">${rec.description}</small>` : ''}
                 </li>
             `).join('')}
         </ul>
     `;
}

function renderCareTips(tips) {
     if (!careTipsList) return;

     if (!tips || tips.length === 0) {
         careTipsList.innerHTML = '<p class="text-muted">Нет советов по уходу.</p>';
         return;
     }

     careTipsList.innerHTML = `
         <div class="accordion" id="careTipsAccordion">
             ${tips.map((tip, index) => `
                 <div class="accordion-item">
                     <h2 class="accordion-header" id="heading${index}">
                         <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
                             ${tip.title || 'Совет'} (${tip.category || 'Общее'})
                         </button>
                     </h2>
                     <div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#careTipsAccordion">
                         <div class="accordion-body">
                             ${tip.content || 'Нет описания.'}
                         </div>
                     </div>
                 </div>
             `).join('')}
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
         saveChangesBtn.childNodes[saveChangesBtn.childNodes.length - 1].nodeValue = ' Сохранить медкарту';
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
        loadRecommendations(currentPetId);
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
        const newIndex = container.children.length;
        const newItemHtml = renderVaccinationInput({ type: '', date: '' }, newIndex);
        container.insertAdjacentHTML('beforeend', newItemHtml);

        const newItemElement = container.lastElementChild;
        newItemElement.querySelector('.remove-vaccination').addEventListener('click', function() {
            newItemElement.remove();
        });
    });

    document.getElementById('vaccinationsEditContainer')?.addEventListener('click', function(event) {
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
            vaccinations: Array.from(document.querySelectorAll('#vaccinationsEditContainer .vaccination-item')).map(vaccItem => ({
                type: vaccItem.querySelector('.vaccination-type').value.trim(),
                date: vaccItem.querySelector('.vaccination-date').value
            })).filter(v => v.type || v.date),
            allergies: document.getElementById('allergiesEdit').value.split(',')
                .map(allergyItem => allergyItem.trim()).filter(a => a !== ''),
            diseases: document.getElementById('diseasesEdit').value.split(',')
                .map(diseaseItem => diseaseItem.trim()).filter(d => d !== '')
        };


        await updateMedicalCard(currentPetId, updatedMedicalCard);

        originalMedicalCard = JSON.parse(JSON.stringify(updatedMedicalCard));
        renderViewMode(originalMedicalCard);
        switchToViewMode();
        showAlert('Изменения медицинской карты успешно сохранены!', 'success');

    } catch (error) {
        console.error('Ошибка при сохранении медкарты:', error);
        showAlert(`Ошибка при сохранении медицинской карты: ${error.message}`, 'danger');
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