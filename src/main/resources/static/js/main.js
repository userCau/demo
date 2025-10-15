document.querySelectorAll('nav.menu-lateral a').forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault(); // evita recarregar a página
        const url = this.getAttribute('href');

        fetch(url)
            .then(response => response.text())
            .then(html => {
                document.getElementById('conteudo-principal').innerHTML = html;
            })
            .catch(err => console.error('Erro ao carregar conteúdo:', err));
    });
});
