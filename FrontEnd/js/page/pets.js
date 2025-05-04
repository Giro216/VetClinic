import { getPets, deletePet, createPet } from "../api/Pets_api.js";

let qrModalInstance = null;
const qrCodeCanvas = document.getElementById('qrCodeCanvas');
const qrCodeUrlElement = document.getElementById('qrCodeUrl');

document.addEventListener("DOMContentLoaded", async () => {
    const qrModalElement = document.getElementById('qrCodeModal');
    if (qrModalElement) {
        qrModalInstance = new bootstrap.Modal(qrModalElement);
    }

    try {
        const pets = await getPets();
        renderPets(pets);
        setupPetCardButtons();
    } catch (error) {
        console.error("Error loading pets:", error);
        displayError(`Ошибка загрузки питомцев: ${error.message}`);
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
        const medCardRelativeUrl = `medCard.html?petId=${petId}`;

        const card = `
            <div class="col-lg-4 col-md-6 mb-4">
                <div class="card h-100">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${petName}</h5>
                        <p class="card-text">
                            ${petId ? `<span class="text-secondary" style="font-size: 0.8em;"><strong>ID:</strong> ${petId}</span><br>` : ''}
                            <strong>Вид:</strong> ${pet.kind || 'Не указан'}<br>
                            <strong>Возраст:</strong> ${(pet.age !== null && pet.age !== undefined) ? pet.age : 'Не указан'}<br>
                        </p>
                        <div class="mt-auto d-flex justify-content-between pt-2">
                            <div>
                                <a href="${medCardRelativeUrl}" class="btn btn-primary btn-sm view-med-card" ${!petId ? 'aria-disabled="true" style="pointer-events: none; opacity: 0.65;"' : ''}>
                                    Медкарта
                                </a>
                                <button class="btn btn-secondary btn-sm generate-qr-btn ms-1" data-pet-id="${petId}" ${!petId ? 'disabled' : ''}>
                                    QR
                                </button>
                            </div>
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

        if (target.classList.contains('generate-qr-btn')) {
            const button = target;
            const petId = button.getAttribute('data-pet-id');
            if (!petId || !qrModalInstance || !qrCodeCanvas) return;

            button.disabled = true;

            try {
                const origin = window.location.origin;
                const pathname = window.location.pathname;
                const directoryPath = pathname.substring(0, pathname.lastIndexOf('/'));
                const absoluteMedCardUrl = `${origin}${directoryPath}/medCard.html?petId=${petId}`;

                qrCodeCanvas.innerHTML = '';

                new QRCode(qrCodeCanvas, {
                    text: absoluteMedCardUrl,
                    width: 180,
                    height: 180,
                    colorDark: "#000000",
                    colorLight: "#ffffff",
                    correctLevel: QRCode.CorrectLevel.H
                });

                 if (qrCodeUrlElement) {
                    qrCodeUrlElement.textContent = "Ссылка";
                    qrCodeUrlElement.href = absoluteMedCardUrl;
                 }

                qrModalInstance.show();

            } catch (error) {
                console.error("Error generating QR code:", error);
                alert("Не удалось сгенерировать QR-код.");
            } finally {
                 button.disabled = false;
            }
        }

        else if (target.classList.contains('delete-pet')) {
            const button = target;
            const petId = button.getAttribute('data-pet-id');
            const petName = button.getAttribute('data-pet-name') || 'этот питомец';
            if (!petId) return;

            if (confirm(`Вы уверены, что хотите удалить питомца "${petName}"?`)) {
                button.disabled = true;
                const originalText = button.textContent;
                button.textContent = 'Удаление...';

                try {
                    await deletePet(petId);
                    const pets = await getPets();
                    renderPets(pets);
                } catch (error) {
                    console.error('Ошибка при удалении питомца:', error);
                    displayError(`Не удалось удалить питомца: ${error.message}`);
                    button.disabled = false;
                    button.textContent = originalText;
                }
            }
        }
    };

    container.addEventListener('click', container._petCardActionListener);
}

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
            age: parseFloat(document.getElementById('petAge').value) || 0
        };

        if (!petData.name) throw new Error("Пожалуйста, введите имя питомца.");
        if (!petData.kind) throw new Error("Пожалуйста, выберите вид питомца.");
        if (isNaN(petData.age) || petData.age < 0) {
             throw new Error("Пожалуйста, введите корректный возраст (0 или больше).");
        }

        await createPet(petData);

        const modal = bootstrap.Modal.getInstance(addPetModalElement);
        addPetForm.reset();

        const handleModalHidden = async () => {
            document.body.style.overflow = 'auto';
            document.body.style.paddingRight = '';
            const backdrops = document.querySelectorAll('.modal-backdrop');
            backdrops.forEach(backdrop => backdrop.remove());
            document.body.classList.remove('modal-open');

            try {
                const pets = await getPets();
                renderPets(pets);
            } catch (fetchError) {
                console.error("Error fetching pets after add:", fetchError);
                displayError(`Не удалось обновить список питомцев: ${fetchError.message}`);
            } finally {
                saveButton.disabled = false;
                saveButton.textContent = 'Сохранить';
            }
        };

        addPetModalElement.removeEventListener('hidden.bs.modal', handleModalHidden);
        addPetModalElement.addEventListener('hidden.bs.modal', handleModalHidden, { once: true });

        if (modal) {
            modal.hide();
        } else {
             saveButton.disabled = false; 
             saveButton.textContent = 'Сохранить';
        }

    } catch (error) {
        alert(`Ошибка при создании питомца: ${error.message || 'Неизвестная ошибка'}`);
        saveButton.disabled = false;
        saveButton.textContent = 'Сохранить';
    }
});

function displayError(message) {
    const container = document.getElementById('petsList');
    if (container) {
        container.innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger">${message}</div>
            </div>
        `;
    } else {
        console.error("Target container 'petsList' not found for error display.");
        alert(message);
    }
}