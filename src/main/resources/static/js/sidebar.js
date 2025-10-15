// Seleciona a sidebar
const sidebar = document.querySelector('nav.menu-lateral');

// Função para expandir ou recolher
function setSidebarState(expanded) {
    if (expanded) {
        sidebar.style.width = '300px';
        localStorage.setItem('sidebarExpanded', 'true');
    } else {
        sidebar.style.width = '70px';
        localStorage.setItem('sidebarExpanded', 'false');
    }
}

// Eventos de hover
sidebar.addEventListener('mouseenter', () => setSidebarState(true));
sidebar.addEventListener('mouseleave', () => setSidebarState(false));

// Ao carregar a página, restaura o estado anterior
window.addEventListener('DOMContentLoaded', () => {
    const expanded = localStorage.getItem('sidebarExpanded') === 'true';
    sidebar.style.width = expanded ? '300px' : '70px';
});
