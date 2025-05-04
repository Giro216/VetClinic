export async function renderNavBar() {
  const container = document.getElementById('nav-bar-main');
  container.innerHTML = '';

  const page =
  `
   <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <a class="navbar-brand" href="/">
            <span class="logo-icon">🐾</span>
            Вет Клиника
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <a class="nav-link" href="/page/pets.html">Питомцы</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/page/appointments.html">Записи</a>
            </li>
          </ul>
           <div class="d-flex">
              <button type="button" class="btn btn-secondary">
                  <a href="/page/authorization.html" class="text-light text-decoration-none">Авторизация</a>
              </button>
              <button type="button" class="btn btn-primary ms-2">
                  <a href="/page/registration.html" class="text-light text-decoration-none">Регистрация</a>
              </button>
           </div>
        </div>
  </nav>
  `

  container.innerHTML = page;
}