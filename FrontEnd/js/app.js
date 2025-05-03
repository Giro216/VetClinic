import { renderNavBar } from "./utils/navBar.js";

// Тестовые данные питомцев
const testPets = [
    {
        "petId": "1234567890abcdef",
        "name": "Барсик",
        "age": 3,
        "kind": "Кот"
    }
];
const testMedCard = [
    {
        "petId": "1234567890abcdef",
        "vaccinations": [
            {
                "date": "2024-03-10",
                "type": "Rabies"
            },
            {
                "date": "2024-04-01",
                "type": "Distemper"
            }
        ],
        "allergies": [
            "Pollen",
            "Chicken"
        ],
        "diseases": [
            "Diabetes",
            "Arthritis"
        ]
    }
];

// Тестовые данные записей
const testAppointments = [
    {
        "id": 123,
        "petId": 456,
        "doctorName": "S. O. Sasd",
        "datetime": "2025-05-15T14:30:00+07:00",
        "status": "CONFIRMED",
        "createdAt": "2025-05-01T10:15:30+07:00"
    }
];

export async function getMedicalCard(petId) {
    return new Promise(resolve => {
        setTimeout(() => {
            const card = testMedCard.find(card => card.petId === petId);
            resolve(card || null);
        }, 500);
    });
}

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


document.addEventListener("DOMContentLoaded", async () => {
    await renderNavBar();
});

// Тестовые данные слотов
const testSlots = [
    {
        id: 1,
        startTime: "2025-05-20T09:00:00",
        availabilities: [
            { doctorId: 1, isAvailable: true },
            { doctorId: 2, isAvailable: false }
        ]
    },
    {
        id: 2,
        startTime: "2025-05-20T10:00:00",
        availabilities: [
            { doctorId: 1, isAvailable: true },
            { doctorId: 2, isAvailable: true }
        ]
    }
];

// Обновленная функция получения слотов
export async function getAvailableSlots(doctorId) {
    return new Promise(resolve => {
        setTimeout(() => {
            const availableSlots = testSlots.filter(slot => 
                slot.availabilities.some(av => 
                    av.doctorId === parseInt(doctorId) && av.isAvailable
                )
            );
            resolve(availableSlots);
        }, 500);
    });
}

// Функция для создания записи
export async function bookAppointment(slotId, doctorId, petId, reason) {
    return new Promise(resolve => {
        setTimeout(() => {
            const slot = testSlots.find(s => s.id === slotId);
            const newAppointment = {
                id: Math.floor(Math.random() * 1000),
                slotId: slotId,
                doctorId: doctorId,
                petId: petId,
                startTime: slot.startTime,
                status: "CONFIRMED",
                reason: reason,
                createdAt: new Date().toISOString()
            };
            testAppointments.push(newAppointment);
            resolve(newAppointment);
        }, 500);
    });
}