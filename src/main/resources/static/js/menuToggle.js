// public/js/menuToggle.js

document.addEventListener("DOMContentLoaded", () => {
    const toggleBtn = document.getElementById("mobile-menu-toggle");
    const menu = document.getElementById("mobile-menu");

    if (toggleBtn && menu) {
        toggleBtn.addEventListener("click", () => {
            menu.classList.toggle("hidden");
            menu.classList.toggle("flex");
        });
    }
});
