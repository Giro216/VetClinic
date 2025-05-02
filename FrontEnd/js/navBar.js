export async function renderNavBar() {
    const container = document.getElementById('nav-bar-main');
    container.innerHTML = ''; // Очищаем контейнер

    const page = 
    `
     <nav class="navbar navbar-expand-lg navbar-light bg-light">
          <a class="navbar-brand" href="/">
              <span class="logo-icon">🐾</span>
              Вет Клиника
          </a>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
              <li class="nav-item">
                <a class="nav-link" href="/page/pets.html">Питомцы</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="/page/appointments.html">Записи</a>
              </li>
            </ul>
          </div>
          <button type="button" class="btn btn-secondary ">
              <a href="/page/authorization.html" class="text-light">Авторизация</a>
          </button>
          <button type="button" class="btn btn-primary ms-2">
            <a href="/page/registration.html" class="text-light">Регистрация</a>
          </button>
    </nav>
    `

    container.innerHTML = page;
}