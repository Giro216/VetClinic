const API_BASE_URL = 'http://localhost:8080/api';

export async function getPets() {
    const response = await fetch(`${API_BASE_URL}/pets/all`, {
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });
    
    if (!response.ok) {
        throw new Error(`Ошибка: ${response.status} ${response.statusText}`);
    }
    return await response.json();
}

export async function createPet(petData) {
    const response = await fetch(`${API_BASE_URL}/pets`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: petData.name,
            kind: petData.kind,
            age: petData.age
        })
    });
    if (!response.ok) throw new Error('Ошибка создания питомца');
    return await response.json();
}

// Работа с медкартами
export async function getMedicalCard(petId) {
    const response = await fetch(`${API_BASE_URL}/cards/${petId}`);
    if (!response.ok) {
        if (response.status === 404) return null;
        throw new Error('Ошибка загрузки медкарты');
    }
    return await response.json();
}

export async function updateMedicalCard(petId, cardData) {
    const response = await fetch(`${API_BASE_URL}/cards/${petId}`, {
        method: 'PUT',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(cardData)
    });
    if (!response.ok) throw new Error('Ошибка обновления медкарты');
    return await response.json();
}