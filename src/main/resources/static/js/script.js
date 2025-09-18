document.addEventListener('DOMContentLoaded', function() {
    const button = document.getElementById('clickMeBtn');
    const message = document.getElementById('message');

    button.addEventListener('click', function() {
        message.textContent = 'Спасибо за клик! Spring Boot отлично работает со статическими файлами! 🎉';
        message.style.display = 'block';

        // Меняем цвет кнопки на короткое время
        button.style.backgroundColor = '#27ae60';
        setTimeout(() => {
            button.style.backgroundColor = '#3498db';
        }, 300);
    });

    // Добавляем текущую дату в футер
    const footer = document.querySelector('footer');
    const dateSpan = document.createElement('span');
    dateSpan.textContent = new Date().toLocaleDateString('ru-RU');
    dateSpan.style.marginLeft = '10px';
    dateSpan.style.fontWeight = 'bold';
    footer.appendChild(dateSpan);
});
