package pedidos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class ItemPedido implements Serializable {
    private static final long serialVersionUID = 1L;
    private String produto;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemPedido(String produto, int quantidade, BigDecimal precoUnitario) {
        this.produto = Objects.requireNonNull(produto);
        setQuantidade(quantidade);
        this.precoUnitario = Objects.requireNonNull(precoUnitario);
    }

    public ItemPedido(ItemPedido origem) {
        this(origem.produto, origem.quantidade, origem.precoUnitario);
    }

    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public String getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setProduto(String produto) { this.produto = Objects.requireNonNull(produto); }
    public void setQuantidade(int quantidade) {
        if (quantidade < 0) throw new IllegalArgumentException("Quantidade negativa");
        this.quantidade = quantidade;
    }
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = Objects.requireNonNull(precoUnitario);
    }
}
