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
    container.innerHTML = ''; // Очищаем контейнер
    
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
                    showMedicalCardModal(medicalCard);
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

function showMedicalCardModal(medicalCard) {
    const modalHtml = `
        <div class="modal fade" id="medicalCardModal" tabindex="-1" aria-labelledby="medicalCardModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="medicalCardModalLabel">Медицинская карта</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <h6>Прививки:</h6>
                        <ul class="list-group mb-3">
                            ${medicalCard.vaccinations.map(vacc => `
                                <li class="list-group-item">
                                    <strong>${vacc.type}</strong> - ${vacc.date}
                                </li>
                            `).join('')}
                        </ul>
                        
                        <h6>Аллергии:</h6>
                        <div class="mb-3">
                            ${medicalCard.allergies.join(', ')}
                        </div>
                        
                        <h6>Хронические заболевания:</h6>
                        <div>
                            ${medicalCard.diseases.join(', ')}
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    //добавляем на страницу
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    //отображаем на странице 
    const modal = new bootstrap.Modal(document.getElementById('medicalCardModal'));
    modal.show();
    
    //удаляем после закрытия
    document.getElementById('medicalCardModal').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}