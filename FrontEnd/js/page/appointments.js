import { getAppointments } from '../app.js';

document.addEventListener("DOMContentLoaded", async () => {
    const appointments = await  getAppointments();


    // Рендерим список записей
    const container = document.getElementById('appointmentsList');
    
    if (appointments.length === 0) {
        container.innerHTML = '<div class="alert alert-info">Нет записей</div>';
        return;
    }

    container.innerHTML = appointments.map(app => {
        return `
        <div class="col-md-4 mb-4">
            <div class="card mb-3">
                <div class="card-body">
                    <h5 class="card-title">Питомец '${app.petName}'</h5>
                    <p class="card-text">
                        <strong>Дата:</strong> ${new Date(app.date).toLocaleString()}<br>
                        <strong>Врач:</strong> ${app.doctor}<br>
                        <strong>Услуга:</strong> ${app.service}<br>
                    </p>
                </div>
            </div>
        </div>    
        `;
    }).join('');
});