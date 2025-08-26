const API_BASE_URL_APPOINTMENTS = 'http://localhost:8082';

export async function getDoctors() {
    try {
        const response = await fetch(`${API_BASE_URL_APPOINTMENTS}/appointments/doctors`, {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP при получении списка докторов: ${response.status} ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error("Ошибка при получении списка докторов:", error);
        throw error;
    }
}

export async function getAppointments() {
    try {
        const response = await fetch(`${API_BASE_URL_APPOINTMENTS}/appointments/`, {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP при получении списка записей: ${response.status} ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error("Ошибка при получении списка записей:", error);
        throw error;
    }
}


export async function getAvailableSlotsByDate(dateString) {
    try {
        const response = await fetch(`${API_BASE_URL_APPOINTMENTS}/appointments/time_slots/available_slots_by_date?date=${dateString}`, {
             credentials: 'include',
             headers: {
                'Accept': 'application/json'
             }
        });

        if (response.status === 204) {
            console.warn(`Нет доступных слотов на дату ${dateString}`);
            return [];
        }

        if (!response.ok) {
             throw new Error(`Ошибка HTTP при получении доступных слотов: ${response.status} ${response.statusText}`);
        }

        return await response.json();

    } catch (error) {
        console.error("Ошибка при получении доступных слотов:", error);
        throw error;
    }
}

export async function bookAppointment(slotId, doctorId, petId, reason) {
    const requestBody = {
        petId: petId, // Передаём как строку UUID
        doctorId: parseInt(doctorId, 10),
        requiredSlotId: parseInt(slotId, 10),
        reason: reason
    };

    try {
        const response = await fetch(`${API_BASE_URL_APPOINTMENTS}/appointments/`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });

        let responseData = null;
        if (response.status !== 204) {
             try {
                 responseData = await response.json();
             } catch (e) {
             }
        }

        if (!response.ok) {
             let errorMessage = `Ошибка HTTP при бронировании: ${response.status} ${response.statusText}`;
             if (responseData && responseData.detail) {
                 errorMessage = `Ошибка бронирования: ${responseData.detail}`;
             } else if (responseData && typeof responseData === 'string') {
                 errorMessage = `Ошибка бронирования: ${responseData}`;
             } else if (responseData) {
                  errorMessage += `: ${JSON.stringify(responseData)}`;
             }
             throw new Error(errorMessage);
        }

        return responseData;

    } catch (error) {
        console.error("Ошибка при бронировании записи:", error);
        throw error;
    }
}