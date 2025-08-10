export const API_BASE_URL = 'http://localhost:8090/api';

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

export async function createPet(petData, petId = null) {
    const body = {
        name: petData.name,
        kind: petData.kind,
        age: petData.age
    };
    if (petId !== null) {
        body.petId = petId;
    }

    const response = await fetch(`${API_BASE_URL}/pets`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    });
    if (!response.ok) {
        let errorMsg = `Ошибка создания питомца: ${response.status} ${response.statusText}`;
         try {
            const errorBody = await response.text();
            if (errorBody) {
                 errorMsg += `, message: ${errorBody}`;
            }
         } catch (e) {}
         throw new Error(errorMsg);
    }
    return await response.json();
}

export async function deletePet(petId) {
    const response = await fetch(`${API_BASE_URL}/pets/${petId}`, {
        method: 'DELETE',
    });
    if (!response.ok && response.status !== 204) {
        let errorMsg = `HTTP error! status: ${response.status}`;
        try {
            const errorBody = await response.text();
            if (errorBody) {
                errorMsg += `, message: ${errorBody}`;
            }
        } catch (e) {
        }
        throw new Error(errorMsg);
    }
}

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

export async function createMedicalCard(petId) {
    try {
        const response = await fetch(`${API_BASE_URL}/cards/${petId}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
             body: null
        });

        if (!response.ok) {
             let errorBody = await response.text();
             try {
                 const jsonError = JSON.parse(errorBody);
                  if (jsonError && jsonError.detail) {
                      errorBody = jsonError.detail;
                  } else if (response.status === 409) {
                      errorBody = "Карта для этого питомца уже существует.";
                  }
             } catch(e) {
             }
            throw new Error(`Ошибка HTTP при создании медкарты: ${response.status} ${response.statusText}. ${errorBody}`);
        }

        return await response.json();

    } catch (error) {
        console.error(`Ошибка при создании медкарты для petId ${petId}:`, error);
        throw error;
    }
}