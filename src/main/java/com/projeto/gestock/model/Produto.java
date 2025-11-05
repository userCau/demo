package com.projeto.gestock.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private Integer quantidade;
    private Double preco;
    private LocalDate validade;

    // =====================================================
    // RELACIONAMENTOS
    // =====================================================

    // Relação com Categoria (muitos produtos para uma categoria)
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Relação com Usuário (quem criou o produto)
    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private Usuario criadoPor;

    // =====================================================
    // CAMPOS DE CONTROLE E EXIBIÇÃO
    // =====================================================

    @Column(name = "estoque_minimo")
    private Integer estoqueMinimo;

    // Campo temporário (não vai para o banco)
    @Transient
    private String status;

    // =====================================================
    // CONSTRUTORES
    // =====================================================

    public Produto() {
    }

    public Produto(String nome, String descricao, Integer quantidade, Double preco,
            LocalDate validade, Categoria categoria, Integer estoqueMinimo, Usuario criadoPor) {
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.preco = preco;
        this.validade = validade;
        this.categoria = categoria;
        this.estoqueMinimo = estoqueMinimo;
        this.criadoPor = criadoPor;
    }

    // =====================================================
    // GETTERS E SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Usuario criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // =====================================================
    // MÉTODOS AUXILIARES
    // =====================================================

    @Transient
    public boolean isEstoqueBaixo() {
        if (this.estoqueMinimo == null || this.quantidade == null)
            return false;
        return this.quantidade <= this.estoqueMinimo;
    }

    @Transient
    public boolean isVencido() {
        if (this.validade == null)
            return false;
        return this.validade.isBefore(LocalDate.now());
    }

    @Transient
    public boolean isProximoVencimento() {
        if (this.validade == null)
            return false;
        LocalDate hoje = LocalDate.now();
        return !this.validade.isBefore(hoje) && this.validade.isBefore(hoje.plusDays(7));
    }
}
