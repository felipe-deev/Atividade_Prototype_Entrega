package pedidos;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Pedido implements Prototype<Pedido>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicLong SEQUENCIA = new AtomicLong(0);
    private final long id;
    private String cliente;
    private String enderecoEntrega;
    private final List<ItemPedido> itens;
    private final LocalDate criadoEm;

    public Pedido(String cliente, String enderecoEntrega) {
        this.id = SEQUENCIA.incrementAndGet();
        this.cliente = Objects.requireNonNull(cliente);
        this.enderecoEntrega = Objects.requireNonNull(enderecoEntrega);
        this.itens = new ArrayList<>();
        this.criadoEm = LocalDate.now();
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(new ItemPedido(Objects.requireNonNull(item)));
    }

    public BigDecimal valorTotal() {
        return itens.stream().map(ItemPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Pedido clonar() {
        Pedido copia = new Pedido(cliente, enderecoEntrega);
        for (ItemPedido item : itens) copia.adicionarItem(item);
        return copia;
    }

    public Pedido clonarPorSerializacao() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (ObjectOutputStream saida = new ObjectOutputStream(buffer)) {
                saida.writeObject(this);
            }
            Pedido desserializado;
            try (ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()))) {
                desserializado = (Pedido) entrada.readObject();
            }
            // A serialização duplica os itens; uma nova criação redefine ID e data.
            Pedido copia = new Pedido(desserializado.cliente, desserializado.enderecoEntrega);
            copia.itens.addAll(desserializado.itens);
            return copia;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Erro ao clonar pedido por serialização", e);
        }
    }

    public long getId() { return id; }
    public String getCliente() { return cliente; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public LocalDate getCriadoEm() { return criadoEm; }
    public List<ItemPedido> getItens() { return java.util.Collections.unmodifiableList(itens); }
    public void setCliente(String cliente) { this.cliente = Objects.requireNonNull(cliente); }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = Objects.requireNonNull(enderecoEntrega); }

    @Override
    public String toString() {
        return "Pedido #" + id + " | " + cliente + " | " + enderecoEntrega
                + " | itens=" + itens.size() + " | total=R$ " + valorTotal().toPlainString().replace('.', ',');
    }
}
