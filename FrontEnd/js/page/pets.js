import { getPets, getMedicalCard, createPet, updateMedicalCard, deletePet } from "../api/Pets_api.js";


document.addEventListener("DOMContentLoaded", async () => {
    try {
        const pets = await getPets(); 
        renderPets(pets);
        setupPetCardButtons();
    } catch (error) {
        console.error("Error loading pets:", error);
        document.getElementById('petsList').innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger">Ошибка загрузки питомцев: ${error.message}</div>
            </div>
        `;
    }
});


function renderPets(pets) {
    const container = document.getElementById('petsList');
    if (!container) return;
    container.innerHTML = '';

    if (!pets || pets.length === 0) {
        container.innerHTML = `<div class="col-12"><p>У вас пока нет добавленных питомцев.</p></div>`;
        return;
    }

    pets.forEach(pet => {
        const petName = pet.name || 'Безымянный';
        const petId = pet.petId;

        const card = `
            <div class="col-md-4 mb-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${petName}</h5>
                        <p class="card-text">
                            ${petId ? `<span class="text-secondary" style="font-size: 0.8em;"><strong>ID:</strong> ${petId}</span><br>` : ''}
                            <strong>Вид:</strong> ${pet.kind || 'Не указан'}<br>
                            <strong>Возраст:</strong> ${(pet.age !== null && pet.age !== undefined) ? pet.age : 'Не указан'}<br>
                        </p>
                        <div class="mt-2 d-flex justify-content-between">
                            <button class="btn btn-primary btn-sm view-med-card" data-pet-id="${petId}" ${!petId ? 'disabled' : ''}>
                                Медкарта
                            </button>
                            <button class="btn btn-danger btn-sm delete-pet" data-pet-id="${petId}" data-pet-name="${petName}" ${!petId ? 'disabled' : ''}>
                                Удалить
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += card;
    });
}

function setupPetCardButtons() {
    const container = document.getElementById('petsList');
    if (!container) return;

    if (container._petCardActionListener) {
        container.removeEventListener('click', container._petCardActionListener);
    }

    container._petCardActionListener = async (event) => {
        const target = event.target;

        if (target.classList.contains('view-med-card')) {
            const button = target;
            const petId = button.getAttribute('data-pet-id');
            if (!petId) return;

            button.disabled = true;
            const originalText = button.textContent;
            button.textContent = 'Загрузка...';

            try {
                const medicalCard = await getMedicalCard(petId);
                if (medicalCard) {
                    medicalCard.vaccinations = medicalCard.vaccinations || [];
                    medicalCard.allergies = medicalCard.allergies || [];
                    medicalCard.diseases = medicalCard.diseases || [];
                    showMedicalCardModal(medicalCard, petId);
                } else {
                    alert('Медкарта не найдена для этого питомца');
                }
            } catch (error) {
                console.error('Ошибка загрузки медкарты:', error);
                alert('Не удалось загрузить медкарту');
            } finally {
                 button.disabled = false;
                 button.textContent = originalText;
            }
        }

        else if (target.classList.contains('delete-pet')) {
            const button = target;
            const petId = button.getAttribute('data-pet-id');
            const petName = button.getAttribute('data-pet-name') || 'этот питомец';
            if (!petId) return;

            if (confirm(`Вы уверены, что хотите удалить питомца "${petName}"?`)) {
                button.disabled = true;
                button.textContent = 'Удаление...';

                try {
                    await deletePet(petId);
                    const pets = await getPets();
                    renderPets(pets);
                    setupPetCardButtons();
                } catch (error) {
                    console.error('Ошибка при удалении питомца:', error);
                    alert(`Не удалось удалить питомца: ${error.message}`);
                    button.disabled = false;
                    button.textContent = 'Удалить';
                }
            }
        }
    };

    container.addEventListener('click', container._petCardActionListener);
}

function showMedicalCardModal(medicalCard, petId) {
    //просмотр и редактирование
    const viewModeHtml = `
        <h6>Прививки:</h6>
        <ul class="list-group mb-3" id="vaccinationsList">
            ${medicalCard.vaccinations.map(vacc => `
                <li class="list-group-item">
                    <strong>${vacc.type}</strong> - ${vacc.date}
                </li>
            `).join('')}
        </ul>
        
        <h6>Аллергии:</h6>
        <div class="mb-3" id="allergiesView">
            ${medicalCard.allergies.join(', ')}
        </div>
        
        <h6>Хронические заболевания:</h6>
        <div id="diseasesView">
            ${medicalCard.diseases.join(', ')}
        </div>
    `;

    const editModeHtml = `
        <h6>Прививки:</h6>
        <div class="mb-3">
            <button class="btn btn-sm btn-success mb-2" id="addVaccinationBtn">+ Добавить прививку</button>
            <div id="vaccinationsEdit">
                ${medicalCard.vaccinations.map((vacc, index) => `
                    <div class="input-group mb-2 vaccination-item">
                        <input type="text" class="form-control" value="${vacc.type}" placeholder="Тип прививки">
                        <input type="date" class="form-control" value="${vacc.date}">
                        <button class="btn btn-outline-danger remove-vaccination" type="button">×</button>
                    </div>
                `).join('')}
            </div>
        </div>
        
        <h6>Аллергии:</h6>
        <div class="mb-3">
            <textarea class="form-control" id="allergiesEdit" rows="2">${medicalCard.allergies.join(', ')}</textarea>
            <small class="text-muted">Перечислите через запятую</small>
        </div>
        
        <h6>Хронические заболевания:</h6>
        <div class="mb-3">
            <textarea class="form-control" id="diseasesEdit" rows="2">${medicalCard.diseases.join(', ')}</textarea>
            <small class="text-muted">Перечислите через запятую</small>
        </div>
    `;

    const modalHtml = `
        <div class="modal fade" id="medicalCardModal" tabindex="-1" aria-labelledby="medicalCardModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="medicalCardModalLabel">Медицинская карта</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div id="viewModeContent">
                            ${viewModeHtml}
                        </div>
                        <div id="editModeContent" style="display: none;">
                            ${editModeHtml}
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                        <button type="button" class="btn btn-outline-primary" id="editToggleBtn">Редактировать</button>
                        <button type="button" class="btn btn-primary" id="saveChangesBtn" style="display: none;">Сохранить</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    const modal = new bootstrap.Modal(document.getElementById('medicalCardModal'));
    const editToggleBtn = document.getElementById('editToggleBtn');
    const saveChangesBtn = document.getElementById('saveChangesBtn');
    const viewModeContent = document.getElementById('viewModeContent');
    const editModeContent = document.getElementById('editModeContent');
    
    editToggleBtn.addEventListener('click', function() {
        viewModeContent.style.display = 'none';
        editModeContent.style.display = 'block';
        editToggleBtn.style.display = 'none';
        saveChangesBtn.style.display = 'block';
    });
    
    saveChangesBtn.addEventListener('click', async function() {
        try {
            const updatedMedicalCard = {
                vaccinations: Array.from(document.querySelectorAll('.vaccination-item')).map(item => ({
                    type: item.querySelector('input[type="text"]').value,
                    date: item.querySelector('input[type="date"]').value
                })),
                allergies: document.getElementById('allergiesEdit').value.split(',').map(item => item.trim()),
                diseases: document.getElementById('diseasesEdit').value.split(',').map(item => item.trim())
            };
            
            await updateMedicalCard(petId, updatedMedicalCard);
            
            viewModeContent.style.display = 'block';
            editModeContent.style.display = 'none';
            editToggleBtn.style.display = 'block';
            saveChangesBtn.style.display = 'none';
            
            viewModeContent.innerHTML = `
                <h6>Прививки:</h6>
                <ul class="list-group mb-3">
                    ${updatedMedicalCard.vaccinations.map(vacc => `
                        <li class="list-group-item">
                            <strong>${vacc.type}</strong> - ${vacc.date}
                        </li>
                    `).join('')}
                </ul>
                
                <h6>Аллергии:</h6>
                <div class="mb-3">
                    ${updatedMedicalCard.allergies.join(', ')}
                </div>
                
                <h6>Хронические заболевания:</h6>
                <div>
                    ${updatedMedicalCard.diseases.join(', ')}
                </div>
            `;
            
            alert('Изменения успешно сохранены');
            
        } catch (error) {
            console.error('Ошибка при сохранении медкарты:', error);
            alert(`Ошибка при сохранении: ${error.message}`);
        }
    });
    
    document.getElementById('addVaccinationBtn')?.addEventListener('click', function() {
        const container = document.getElementById('vaccinationsEdit');
        const newItem = document.createElement('div');
        newItem.className = 'input-group mb-2 vaccination-item';
        newItem.innerHTML = `
            <input type="text" class="form-control" placeholder="Тип прививки">
            <input type="date" class="form-control">
            <button class="btn btn-outline-danger remove-vaccination" type="button">×</button>
        `;
        container.appendChild(newItem);
        
        newItem.querySelector('.remove-vaccination').addEventListener('click', function() {
            container.removeChild(newItem);
        });
    });
    
    document.querySelectorAll('.remove-vaccination').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.vaccination-item').remove();
        });
    });
    
    modal.show();
    
    document.getElementById('medicalCardModal').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}

//обработчик сохранения нового питомца
document.getElementById('savePetBtn')?.addEventListener('click', async () => {
    const saveButton = document.getElementById('savePetBtn');
    const addPetModalElement = document.getElementById('addPetModal');
    const addPetForm = document.getElementById('addPetForm');

    if (!addPetForm.checkValidity()) {
         addPetForm.reportValidity();
         return;
    }

    saveButton.disabled = true;
    saveButton.textContent = 'Сохранение...';

    try {
        const petData = {
            name: document.getElementById('petName').value.trim(),
            kind: document.getElementById('petKind').value,
            age: parseInt(document.getElementById('petAge').value, 10) || 0
        };

        await createPet(petData);

        const modal = bootstrap.Modal.getInstance(addPetModalElement);

        addPetForm.reset();

        modal.hide();

        addPetModalElement.addEventListener('hidden.bs.modal', async () => {

            const backdrops = document.querySelectorAll('.modal-backdrop');
            backdrops.forEach(backdrop => backdrop.remove());
            document.body.classList.remove('modal-open');
            document.body.style.overflow = 'auto';
            document.body.style.paddingRight = '0';

            const pets = await getPets();
            renderPets(pets);
            setupMedicalCardButtons();

            saveButton.disabled = false;
            saveButton.textContent = 'Сохранить';

        }, { once: true });


    } catch (error) {
        console.error('Error creating pet:', error);
        alert(`Ошибка при создании питомца: ${error.message || 'Неизвестная ошибка'}`);
        saveButton.disabled = false;
        saveButton.textContent = 'Сохранить';
    }
});