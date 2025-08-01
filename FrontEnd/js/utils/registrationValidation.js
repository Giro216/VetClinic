document.getElementById('registrationForm').addEventListener('submit', function(event) {
    const form = this;
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    
    password.setCustomValidity("");
    confirmPassword.setCustomValidity("");
    
    // проверка длины
    if (password.value.length < 8) {
        password.setCustomValidity("Пароль должен содержать минимум 8 символов");
    }
    
    //проверка совпадений паролей
    if (password.value !== confirmPassword.value) {
        confirmPassword.setCustomValidity("Пароли не совпадают");
    }
    
    if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
    }
    
    form.classList.add('was-validated');
    
    if (form.checkValidity()) {
        alert('Регистрация успешна!');
        // form.submit(); //для реальной отправки
    }
});