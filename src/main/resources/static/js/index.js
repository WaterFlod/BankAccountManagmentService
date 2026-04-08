(function() {
    const canvas = document.getElementById('wave-canvas');
    const ctx = canvas.getContext('2d');
    let width, height;
    let time = 0;

    function resizeCanvas() {
        width = window.innerWidth;
        height = window.innerHeight;
        canvas.width = width;
        canvas.height = height;
    }

    function drawSilverWaves() {
        if (!ctx) return;
        ctx.clearRect(0, 0, width, height);
        // серебряные волны на черном — градиенты + линии
        const grad = ctx.createLinearGradient(0, 0, width*0.8, height*0.4);
        grad.addColorStop(0, 'rgba(192, 192, 192, 0.1)');
        grad.addColorStop(0.5, 'rgba(212, 175, 55, 0.18)');
        grad.addColorStop(1, 'rgba(160, 160, 160, 0.05)');
        ctx.fillStyle = grad;
        ctx.fillRect(0, 0, width, height);

            // несколько серебристых волн
        for (let i = 0; i < 5; i++) {
            ctx.beginPath();
            const amplitude = 18 + i * 5;
            const period = 0.008 + i * 0.002;
            const phase = time * 0.7 + i * 1.2;
            const yOffset = height * (0.2 + i * 0.12);

            ctx.moveTo(0, yOffset + Math.sin(phase) * amplitude);
            for (let x = 0; x <= width; x += 30) {
                const y = yOffset + Math.sin(x * period + phase) * amplitude + Math.sin(x * 0.003 + time) * 6;
                ctx.lineTo(x, y);
            }
            const opacity = 0.22 - i * 0.03;
            ctx.strokeStyle = `rgba(212, 175, 55, ${0.25 - i*0.03})`;
            ctx.lineWidth = 1.2 + (4-i)*0.3;
            ctx.stroke();

            // дополнительная серебряная волна
            ctx.beginPath();
            const amp2 = 12 + i * 2;
            const yOff2 = height * (0.45 + i * 0.1);
            for (let x = 0; x <= width; x += 25) {
                const y2 = yOff2 + Math.sin(x * 0.012 + time * 0.9 + i) * amp2 + Math.cos(x * 0.005) * 4;
                ctx.lineTo(x, y2);
            }
                ctx.strokeStyle = `rgba(192, 192, 192, ${0.2 - i*0.02})`;
                ctx.lineWidth = 1;
                ctx.stroke();
            }

            // мерцающие точки (серебряные блики)
            for (let s = 0; s < 70; s++) {
                if (s % 2 === 0) continue;
                const x = (s * 131) % width;
                const y = (s * 253 + time * 15) % height;
                ctx.beginPath();
                ctx.arc(x, y, 1.2, 0, Math.PI * 2);
                ctx.fillStyle = `rgba(212, 175, 55, ${0.3 + Math.sin(time + s)*0.2})`;
                ctx.fill();
            }
            requestAnimationFrame(function animateWaves() {
                time += 0.025;
                drawSilverWaves();
            });
        }

        window.addEventListener('resize', () => {
            resizeCanvas();
            drawSilverWaves();
        });
        resizeCanvas();
        drawSilverWaves();
})();