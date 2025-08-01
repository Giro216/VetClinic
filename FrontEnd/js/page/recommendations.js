import { createCareTip, createFoodRecommendation } from "../api/recommendation_api.js";

const addCareTipForm = document.getElementById('addCareTipForm');
const saveCareTipBtn = document.getElementById('saveCareTipBtn');
const addFoodForm = document.getElementById('addFoodForm');
const saveFoodBtn = document.getElementById('saveFoodBtn');
const alertPlaceholder = document.getElementById('alertPlaceholder');

const careTipSpeciesSelect = document.getElementById('careTipSpecies');
const foodSpeciesSelect = document.getElementById('foodSpecies');


document.addEventListener("DOMContentLoaded", () => {
    setupEventListeners();
});


function setupEventListeners() {
    addCareTipForm?.addEventListener('submit', handleAddCareTip);
    addFoodForm?.addEventListener('submit', handleAddFood);
}


function getSelectedValues(selectElement) {
    if (!selectElement) return [];
    return Array.from(selectElement.selectedOptions).map(option => option.value).filter(value => value.trim() !== '');
}


async function handleAddCareTip(event) {
    event.preventDefault();

    clearAlert();

    const title = document.getElementById('careTipTitle').value.trim();
    const content = document.getElementById('careTipContent').value.trim();
    const category = document.getElementById('careTipCategory').value.trim();
    const minAgeMonthsInput = document.getElementById('careTipMinAge');
    const maxAgeMonthsInput = document.getElementById('careTipMaxAge');

    const applicableSpeciesNames = getSelectedValues(careTipSpeciesSelect);

    const applicableBreedNames = [];
    const relevantAllergyNames = [];

    const minAgeMonths = minAgeMonthsInput.value ? parseInt(minAgeMonthsInput.value, 10) : null;
    const maxAgeMonths = maxAgeMonthsInput.value ? parseInt(maxAgeMonthsInput.value, 10) : null;

    if (!title || !content || !category) {
        showAlert('Пожалуйста, заполните все обязательные поля (Заголовок, Содержание, Категория).', 'warning');
        return;
    }
    if ((minAgeMonths !== null && isNaN(minAgeMonths)) || (maxAgeMonths !== null && isNaN(maxAgeMonths))) {
        showAlert('Минимальный и максимальный возраст должны быть числами.', 'warning');
        return;
    }
     if (minAgeMonths !== null && minAgeMonths < 0) {
         showAlert('Минимальный возраст не может быть отрицательным.', 'warning');
         return;
     }
      if (maxAgeMonths !== null && maxAgeMonths < 0) {
          showAlert('Максимальный возраст не может быть отрицательным.', 'warning');
          return;
      }
     if (minAgeMonths !== null && maxAgeMonths !== null && minAgeMonths > maxAgeMonths) {
         showAlert('Минимальный возраст не может быть больше максимального.', 'warning');
         return;
     }


    const careTipData = {
        title: title,
        content: content,
        category: category,
        minAgeMonths: minAgeMonths,
        maxAgeMonths: maxAgeMonths,
        applicableSpeciesNames: applicableSpeciesNames,
        applicableBreedNames: applicableBreedNames,
        relevantAllergyNames: relevantAllergyNames
    };

    setButtonLoading(saveCareTipBtn, true);

    try {
        const createdTip = await createCareTip(careTipData);
        console.log('Совет по уходу успешно создан:', createdTip);

        showAlert('Совет по уходу успешно добавлен!', 'success');
        addCareTipForm.reset();
        Array.from(careTipSpeciesSelect.options).forEach(option => option.selected = false);


    } catch (error) {
        console.error('Ошибка при создании совета по уходу:', error);
        showAlert(`Ошибка при добавлении совета по уходу: ${error.message}`, 'danger');
    } finally {
        setButtonLoading(saveCareTipBtn, false);
    }
}


async function handleAddFood(event) {
    event.preventDefault();

    clearAlert();

    const name = document.getElementById('foodName').value.trim();
    const brand = document.getElementById('foodBrand').value.trim();
    const description = document.getElementById('foodDescription').value.trim();
    const minAgeMonthsInput = document.getElementById('foodMinAge');
    const maxAgeMonthsInput = document.getElementById('foodMaxAge');

    const targetSpeciesNames = getSelectedValues(foodSpeciesSelect);

    const targetBreedNames = [];
    const ingredientNames = [];

    const minAgeMonths = minAgeMonthsInput.value ? parseInt(minAgeMonthsInput.value, 10) : null;
    const maxAgeMonths = maxAgeMonthsInput.value ? parseInt(maxAgeMonthsInput.value, 10) : null;

    if (!name || !brand) {
        showAlert('Пожалуйста, заполните обязательные поля (Название корма, Бренд).', 'warning');
        return;
    }
     if ((minAgeMonths !== null && isNaN(minAgeMonths)) || (maxAgeMonths !== null && isNaN(maxAgeMonths))) {
        showAlert('Минимальный и максимальный возраст должны быть числами.', 'warning');
        return;
    }
     if (minAgeMonths !== null && minAgeMonths < 0) {
         showAlert('Минимальный возраст не может быть отрицательным.', 'warning');
         return;
     }
      if (maxAgeMonths !== null && maxAgeMonths < 0) {
          showAlert('Максимальный возраст не может быть отрицательным.', 'warning');
          return;
      }
     if (minAgeMonths !== null && maxAgeMonths !== null && minAgeMonths > maxAgeMonths) {
         showAlert('Минимальный возраст не может быть больше максимального.', 'warning');
         return;
     }


    const foodData = {
        name: name,
        brand: brand,
        description: description || null,
        minAgeMonths: minAgeMonths,
        maxAgeMonths: maxAgeMonths,
        targetSpeciesNames: targetSpeciesNames,
        targetBreedNames: targetBreedNames,
        ingredientNames: ingredientNames
    };

    setButtonLoading(saveFoodBtn, true);

    try {
        const createdFood = await createFoodRecommendation(foodData);
        console.log('Рекомендация корма успешно создана:', createdFood);

        showAlert('Рекомендация корма успешно добавлена!', 'success');
        addFoodForm.reset();
         Array.from(foodSpeciesSelect.options).forEach(option => option.selected = false);

    } catch (error) {
        console.error('Ошибка при создании рекомендации корма:', error);
        showAlert(`Ошибка при добавлении рекомендации корма: ${error.message}`, 'danger');
    } finally {
        setButtonLoading(saveFoodBtn, false);
    }
}

function setButtonLoading(button, isLoading) {
    const spinner = button.querySelector('.spinner-border');
    const originalText = button.getAttribute('data-original-text') || button.textContent;
    if (!button.hasAttribute('data-original-text')) {
        button.setAttribute('data-original-text', button.textContent);
    }

    if (isLoading) {
        button.disabled = true;
        spinner.style.display = 'inline-block';
        const buttonTextNode = button.childNodes[button.childNodes.length - 1];
        if (buttonTextNode.nodeType === Node.TEXT_NODE) {
             buttonTextNode.nodeValue = ' Сохранение...';
        } else {
             button.textContent = ' Сохранение...';
        }
    } else {
        button.disabled = false;
        spinner.style.display = 'none';
        const buttonTextNode = button.childNodes[button.childNodes.length - 1];
         if (buttonTextNode.nodeType === Node.TEXT_NODE) {
               if (button.id === 'saveCareTipBtn') buttonTextNode.nodeValue = ' Сохранить Совет';
               else if (button.id === 'saveFoodBtn') buttonTextNode.nodeValue = ' Сохранить Корм';
               else button.textContent = button.id.startsWith('saveCareTip') ? ' Сохранить Совет' : ' Сохранить Корм';
         } else {
             if (button.id === 'saveCareTipBtn') button.textContent = ' Сохранить Совет';
             else if (button.id === 'saveFoodBtn') button.textContent = ' Сохранить Корм';
         }
    }
}


function showAlert(message, type = 'danger') {
    const wrapper = document.createElement('div');
    wrapper.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    alertPlaceholder.innerHTML = '';
    alertPlaceholder.append(wrapper);
}

function clearAlert() {
    alertPlaceholder.innerHTML = '';
}