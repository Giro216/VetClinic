import { getPets } from "../app.js";

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const pets = await getPets(); 
        renderPets(pets);
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
                            <span class="text-secondary"><strong>id:</strong>  ${pet.id}</span><br>
                            <strong>Вид:</strong> ${pet.species}<br>
                            <strong>Парода:</strong> ${pet.breed}<br>
                            <strong>Возраст:</strong> ${pet.age}<br>
                        </p>
                        <div class="container">
                            <a href="#" class="btn btn-primary btn-sm">Открыть медкарту</a>
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += card;
    });
}