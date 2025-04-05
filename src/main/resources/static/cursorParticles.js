function initCursorParticles() {
  const canvas = document.createElement("canvas");
  canvas.style.position = "absolute";
  canvas.style.top = "0";
  canvas.style.left = "0";
  canvas.style.pointerEvents = "none"; // Prevents interaction
  document.body.appendChild(canvas);
  canvas.style.zIndex = "100"; // Higher than navbar
  canvas.style.pointerEvents = "none"; // Prevents blocking clicks
  const ctx = canvas.getContext("2d");

  function resizeCanvas() {
    canvas.width = document.documentElement.scrollWidth;
    canvas.height = document.documentElement.scrollHeight;
  }
  resizeCanvas();
  window.addEventListener("resize", resizeCanvas);

  const particles = [];

  class Particle {
    constructor(x, y) {
      this.x = x;
      this.y = y;
      this.size = Math.random() * 4 + 1;
      this.speedX = Math.random() * 2 - 1;
      this.speedY = Math.random() * 2 - 1;
      this.color = `hsl(${Math.random() * 360}, 100%, 50%)`;
    }

    update() {
      this.x += this.speedX;
      this.y += this.speedY;
      this.size *= 0.95;
    }

    draw() {
      ctx.fillStyle = this.color;
      ctx.beginPath();
      ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
      ctx.fill();
    }
  }

  function handleParticles() {
    for (let i = 0; i < particles.length; i++) {
      particles[i].update();
      particles[i].draw();

      if (particles[i].size <= 0.5) {
        particles.splice(i, 1);
        i--;
      }
    }
  }

  window.addEventListener("mousemove", (event) => {
    const scrollX = window.scrollX;
    const scrollY = window.scrollY;
    for (let i = 0; i < 3; i++) {
      particles.push(
        new Particle(event.clientX + scrollX, event.clientY + scrollY)
      );
    }
  });

  function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    handleParticles();
    requestAnimationFrame(animate);
  }

  animate();
}

document.addEventListener("DOMContentLoaded", initCursorParticles);
