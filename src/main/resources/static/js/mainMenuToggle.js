const menuToggle = document.getElementById('menu-toggle');
const mobileMenu = document.getElementById('mobile-menu');

menuToggle.addEventListener('click', () => {
	mobileMenu.classList.toggle('hidden');
});

document.addEventListener("DOMContentLoaded", function() {
	const hamburger = document.getElementById("hamburger");
	const mobileNav = document.getElementById("mobile-nav");

	hamburger.addEventListener("click", function() {
		mobileNav.classList.toggle("hidden");
	});
});