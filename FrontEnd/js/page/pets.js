import { getPets, getMedicalCard } from "../app.js";

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const pets = await getPets(); 
        renderPets(pets);
        setupMedicalCardButtons();
    } catch (error) {
        console.error("Error loading pets:", error);
        document.getElementById('petsList').innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger">Ошибка загрузки питомцев</div>
            </div>
        `;
    }
});

function renderPets(pets) {
    const container = document.getElementById('petsList');
    container.innerHTML = '';
    
    pets.forEach(pet => {
        const card = `
            <div class="col-md-4 mb-4">
                <div class="card">
                    <div class="card-body row">
                        <h5 class="card-title">${pet.name}</h5>
                        <p class="card-text">
                            <span class="text-secondary"><strong>id:</strong> ${pet.petId}</span><br>
                            <strong>Вид:</strong> ${pet.kind}<br>
                            <strong>Возраст:</strong> ${pet.age}<br>
                        </p>
                        <div class="container">
                            <button class="btn btn-primary btn-sm view-med-card" data-pet-id="${pet.petId}">
                                Открыть медкарту
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += card;
    });
}

function setupMedicalCardButtons() {
    document.querySelectorAll('.view-med-card').forEach(button => {
        button.addEventListener('click', async function() {
            const petId = this.getAttribute('data-pet-id');
            try {
                const medicalCard = await getMedicalCard(petId);
                
                if (medicalCard) {
                    showMedicalCardModal(medicalCard, petId);
                } else {
                    alert('Медкарта не найдена для этого питомца');
                }
            } catch (error) {
                console.error('Ошибка загрузки медкарты:', error);
                alert('Не удалось загрузить медкарту');
            }
        });
    });
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
            
            //await updateMedicalCard(petId, updatedMedicalCard);
            
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
            alert('Не удалось сохранить изменения');
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