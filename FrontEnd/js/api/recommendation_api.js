const API_BASE_URL_RECOMMENDATIONS = 'http://localhost:8083/api/v1';

export async function getFoodRecommendations(petId) {
    if (!petId) {
        console.error("Не указан ID питомца для получения рекомендаций по корму.");
        return [];
    }
    try {
        const response = await fetch(`${API_BASE_URL_RECOMMENDATIONS}/pets/${petId}/food-recommendations`, {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.status === 404) {
             return [];
        }

        if (!response.ok) {
            throw new Error(`Ошибка HTTP при получении рекомендаций по корму: ${response.status} ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Ошибка при получении рекомендаций по корму для питомца ${petId}:`, error);
        throw error;
    }
}

export async function getCareTips(petId) {
     if (!petId) {
        console.error("Не указан ID питомца для получения советов по уходу.");
        return [];
    }
    try {
        const response = await fetch(`${API_BASE_URL_RECOMMENDATIONS}/pets/${petId}/care-tips`, {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.status === 404) {
            return [];
        }


        if (!response.ok) {
            throw new Error(`Ошибка HTTP при получении советов по уходу: ${response.status} ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Ошибка при получении советов по уходу для питомца ${petId}:`, error);
        throw error;
    }
}

const CURRENT_USER_ID = 1;

export async function getReminders(userId = CURRENT_USER_ID, status = null) {
    try {
        let url = `${API_BASE_URL_RECOMMENDATIONS}/users/${userId}/reminders`;
        if (status) {
            url += `?status=${encodeURIComponent(status)}`;
        }
        const response = await fetch(url, {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            let errorBody = await response.text();
            try {
                const jsonError = JSON.parse(errorBody);
                 if (jsonError && jsonError.detail) {
                     errorBody = jsonError.detail;
                 }
            } catch(e) {
            }
            throw new Error(`Ошибка HTTP при получении напоминаний: ${response.status} ${response.statusText}. ${errorBody}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Ошибка при получении напоминаний для пользователя ${userId}:`, error);
        throw error;
    }
}

export async function createReminder(userId = CURRENT_USER_ID, reminderData) {
     if (!reminderData || !reminderData.type || !reminderData.dueDate || !reminderData.petId) {
        console.error("Неполные данные для создания напоминания.");
        throw new Error("Неполные данные для создания напоминания.");
     }
    try {
        const response = await fetch(`${API_BASE_URL_RECOMMENDATIONS}/users/${userId}/reminders`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(reminderData)
        });

        if (!response.ok) {
             let errorBody = await response.text();
             try {
                 const jsonError = JSON.parse(errorBody);
                  if (jsonError && jsonError.detail) {
                      errorBody = jsonError.detail;
                  }
             } catch(e) {
             }
            throw new Error(`Ошибка HTTP при создании напоминания: ${response.status} ${response.statusText}. ${errorBody}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Ошибка при создании напоминания для пользователя ${userId}:`, error);
        throw error;
    }
}

export async function updateReminder(userId = CURRENT_USER_ID, reminderId, updateData) {
     if (!reminderId) {
         console.error("Не указан ID напоминания для обновления.");
         throw new Error("Не указан ID напоминания для обновления.");
     }
    try {
        const response = await fetch(`${API_BASE_URL_RECOMMENDATIONS}/users/${userId}/reminders/${reminderId}`, {
            method: 'PATCH',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(updateData)
        });

        if (!response.ok) {
             let errorBody = await response.text();
             try {
                 const jsonError = JSON.parse(errorBody);
                  if (jsonError && jsonError.detail) {
                      errorBody = jsonError.detail;
                  }
             } catch(e) {
             }
            throw new Error(`Ошибка HTTP при обновлении напоминания: ${response.status} ${response.statusText}. ${errorBody}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Ошибка при обновлении напоминания ${reminderId} для пользователя ${userId}:`, error);
        throw error;
    }
}

export async function deleteReminder(userId = CURRENT_USER_ID, reminderId) {
     if (!reminderId) {
         console.error("Не указан ID напоминания для удаления.");
         throw new Error("Не указан ID напоминания для удаления.");
     }
    try {
        const response = await fetch(`${API_BASE_URL_RECOMMENDATIONS}/users/${userId}/reminders/${reminderId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok && response.status !== 204) {
             let errorBody = await response.text();
             try {
                 const jsonError = JSON.parse(errorBody);
                  if (jsonError && jsonError.detail) {
                      errorBody = jsonError.detail;
                  }
             } catch(e) {
             }
            throw new Error(`Ошибка HTTP при удалении напоминания: ${response.status} ${response.statusText}. ${errorBody}`);
        }

        if (response.status !== 204) {
             return await response.json();
        }


    } catch (error) {
        console.error(`Ошибка при удалении напоминания ${reminderId} для пользователя ${userId}:`, error);
        throw error;
    }
}

export async function getReminderById(userId = CURRENT_USER_ID, reminderId) {
    if (!reminderId) {
        console.error("Не указан ID напоминания для получения.");
        throw new Error("Не указан ID напоминания для получения.");
    }
   try {
       const response = await fetch(`${API_BASE_URL_RECOMMENDATIONS}/users/${userId}/reminders/${reminderId}`, {
           credentials: 'include',
           headers: {
               'Accept': 'application/json'
           }
       });

       if (!response.ok) {
            let errorBody = await response.text();
            try {
                const jsonError = JSON.parse(errorBody);
                 if (jsonError && jsonError.detail) {
                     errorBody = jsonError.detail;
                 }
            } catch(e) {
            }
           throw new Error(`Ошибка HTTP при получении напоминания: ${response.status} ${response.statusText}. ${errorBody}`);
       }

       return await response.json();
   } catch (error) {
       console.error(`Ошибка при получении напоминания ${reminderId} для пользователя ${userId}:`, error);
       throw error;
   }
}

