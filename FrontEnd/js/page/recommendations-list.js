import { getAllCareTips, getAllFoodRecommendations } from "../api/recommendation_api.js";

document.addEventListener("DOMContentLoaded", async () => {

    const careTipsList = document.getElementById('careTipsList');
    const foodRecommendationsList = document.getElementById('foodRecommendationsList');

    const careTipsLoading = document.getElementById('careTipsLoading');
    const foodRecommendationsLoading = document.getElementById('foodRecommendationsLoading');

    const careTipsError = document.getElementById('careTipsError');
    const foodRecommendationsError = document.getElementById('foodRecommendationsError');

    const careTipsEmpty = document.getElementById('careTipsEmpty');
    const foodRecommendationsEmpty = document.getElementById('foodRecommendationsEmpty');

    const careTipsTab = document.getElementById('care-tips-tab');
    const foodRecommendationsTab = document.getElementById('food-recommendations-tab');

    function resetDisplay(type) {
        if (type === 'careTips') {
            if(careTipsList) careTipsList.innerHTML = '';
            if(careTipsLoading) careTipsLoading.style.display = 'none';
            if(careTipsError) careTipsError.style.display = 'none';
            if(careTipsEmpty) careTipsEmpty.style.display = 'none';
        } else if (type === 'foodRecommendations') {
            if(foodRecommendationsList) foodRecommendationsList.innerHTML = '';
            if(foodRecommendationsLoading) foodRecommendationsLoading.style.display = 'none';
            if(foodRecommendationsError) foodRecommendationsError.style.display = 'none';
            if(foodRecommendationsEmpty) foodRecommendationsEmpty.style.display = 'none';
        }
    }

    function showError(type, message) {
         resetDisplay(type);
         const errorElement = type === 'careTips' ? careTipsError : foodRecommendationsError;
         if (errorElement) {
             errorElement.textContent = `Ошибка при загрузке: ${message}`;
             errorElement.style.display = 'block';
         }
    }

     function showEmpty(type) {
         resetDisplay(type);
         const emptyElement = type === 'careTips' ? careTipsEmpty : foodRecommendationsEmpty;
         if (emptyElement) {
            emptyElement.style.display = 'block';
         }
     }

    function showLoading(type) {
        resetDisplay(type);
        const loadingElement = type === 'careTips' ? careTipsLoading : foodRecommendationsLoading;
        if (loadingElement) {
            loadingElement.style.display = 'block';
        }
    }

    function renderCareTips(careTips) {
        resetDisplay('careTips');
        if (!careTipsList) return;

        if (!careTips || careTips.length === 0) {
            showEmpty('careTips');
            return;
        }

        careTips.forEach(tip => {
            const colDiv = document.createElement('div');
            colDiv.classList.add('col-md-6', 'col-lg-4');

            const card = document.createElement('div');
            card.classList.add('card', 'recommendation-card', 'mb-3');

            card.innerHTML = `
                <div class="card-body">
                    <h5 class="card-title"><span class="fw-bold">Заголовок:</span> ${tip.title || 'Без заголовка'}</h5>
                    <h6 class="card-subtitle mb-2 text-muted"><span class="fw-bold">Категория:</span> ${tip.category || 'Без категории'}</h6>
                    <p class="card-text"><span class="fw-bold">Содержание:</span> ${tip.content || 'Нет содержания.'}</p>
                    ${(tip.applicableSpeciesNames && tip.applicableSpeciesNames.length > 0) ||
                      (tip.applicableBreedNames && tip.applicableBreedNames.length > 0) ||
                      (tip.relevantAllergyNames && tip.relevantAllergyNames.length > 0) ?
                        `<p class="card-text text-muted mt-2">
                            <span class="fw-bold">Применимо:</span><br>
                            ${tip.applicableSpeciesNames && tip.applicableSpeciesNames.length > 0 ? `Виды: ${tip.applicableSpeciesNames.join(', ')}<br>` : ''}
                            ${tip.applicableBreedNames && tip.applicableBreedNames.length > 0 ? `Породы: ${tip.applicableBreedNames.join(', ')}<br>` : ''}
                            ${tip.relevantAllergyNames && tip.relevantAllergyNames.length > 0 ? `Аллергии: ${tip.relevantAllergyNames.join(', ')}` : ''}
                        </p>`
                        : `<p class="card-text text-muted mt-2"></p>`
                    }
                </div>
            `;
            careTipsList.appendChild(colDiv).appendChild(card);
        });
    }

    function renderFoodRecommendations(foodRecommendations) {
        resetDisplay('foodRecommendations');
         if (!foodRecommendationsList) return;

        if (!foodRecommendations || foodRecommendations.length === 0) {
            showEmpty('foodRecommendations');
            return;
        }

        foodRecommendations.forEach(food => {
             const colDiv = document.createElement('div');
            colDiv.classList.add('col-md-6', 'col-lg-4');

            const card = document.createElement('div');
            card.classList.add('card', 'recommendation-card', 'mb-3');

            card.innerHTML = `
                <div class="card-body">
                    <h5 class="card-title"><span class="fw-bold">Название:</span> ${food.name || 'Без названия'}</h5>
                    <h6 class="card-subtitle mb-2 text-muted"><span class="fw-bold">Бренд:</span> ${food.brand || 'Без бренда'}</h6>
                    ${food.description ? `<p class="card-text"><span class="fw-bold">Описание:</span> ${food.description}</p>` : ''}
                    <p class="card-text text-muted mt-2">
                        ${food.targetSpeciesNames && food.targetSpeciesNames.length > 0 ?
                            `<span class="fw-bold">Виды:</span> ${food.targetSpeciesNames.join(', ')}<br>` : ''
                        }
                         ${food.targetBreedNames && food.targetBreedNames.length > 0 ?
                             `<span class="fw-bold">Породы:</span> ${food.targetBreedNames.join(', ')}<br>` : ''
                         }
                         ${food.ingredientNames && food.ingredientNames.length > 0 ?
                             `<span class="fw-bold">Ингредиенты:</span> ${food.ingredientNames.join(', ')}` : ''
                         }
                    </p>
                </div>
            `;
            foodRecommendationsList.appendChild(colDiv).appendChild(card);
        });
    }

    async function loadCareTips() {
        showLoading('careTips');
        try {
            const careTips = await getAllCareTips();
            renderCareTips(careTips);
        } catch (error) {
            console.error("Ошибка загрузки советов:", error);
            showError('careTips', error.message);
        }
    }

    async function loadFoodRecommendations() {
         showLoading('foodRecommendations');
        try {
            const foodRecommendations = await getAllFoodRecommendations();
            renderFoodRecommendations(foodRecommendations);
        } catch (error) {
            console.error("Ошибка загрузки рекомендаций корма:", error);
            showError('foodRecommendations', error.message);
        }
    }

    const recommendationsTabsEl = document.getElementById('recommendationsTabs');
    if (recommendationsTabsEl) {
         recommendationsTabsEl.addEventListener('shown.bs.tab', event => {
            const targetId = event.target.getAttribute('data-bs-target');
            if (targetId === '#care-tips-pane') {
                loadCareTips();
            } else if (targetId === '#food-recommendations-pane') {
                loadFoodRecommendations();
            }
        });
    }

    loadCareTips();
});