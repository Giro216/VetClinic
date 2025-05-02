import { renderNavBar } from "./utils/navBar.js";

// Тестовые данные питомцев
const testPets = [
    {
        id: '1',
        name: 'Барсик',
        species: 'cat',
        breed: 'Британский',
        age: 3,
        chipId: 'CHIP123',
        medicalCard: {
          vaccinations: [
            { type: 'Комплексная', date: '2023-05-10', validUntil: '2024-05-10' }
          ],
          allergies: ['пыльца']
        }
    },
    {
        id: '2',
        name: "Шарик",
        species: "dog",
        breed: "Дворняга",
        age: 5
    }
];

// Тестовые данные записей
const testAppointments = [
    {
        id: '1',
        date: '2023-12-15T14:30:00',
        doctor: 'Иванова А.П.',
        service: 'Ежегодный осмотр',
        petName: "Шпулька"
    },
    {
        id: '2',
        date: '2023-12-16T10:00:00',
        doctor: 'Петров В.С.',
        service: 'Вакцинация',
        petName: 'Шершень'
    }
];

// Функции для работы с питомцами
export async function getPets() {
    return new Promise(resolve => {
        setTimeout(() => resolve(testPets), 500);
    });
}

// Функции для работы с записями
export async function getAppointments() {
    return new Promise(resolve => {
        setTimeout(() => resolve(testAppointments), 500);
    });
}

export async function getAppointmentById(id) {
    return new Promise(resolve => {
        setTimeout(() => {
            resolve(testAppointments.find(app => app.id === id));
        }, 500);
    });
}

document.addEventListener("DOMContentLoaded", async () => {
    await renderNavBar();
});