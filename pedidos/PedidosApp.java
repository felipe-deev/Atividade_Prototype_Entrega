package pedidos;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class PedidosApp {
    private static BigDecimal dinheiro(String valor) { return new BigDecimal(valor); }
    private static boolean igual(Pedido pedido, String valor) {
        return pedido.valorTotal().compareTo(dinheiro(valor)) == 0;
    }
    private static void verificar(String codigo, String descricao, boolean passou) {
        System.out.println(codigo + " " + descricao + ": " + (passou ? "OK" : "FALHA"));
    }
    private static boolean itensIndependentes(Pedido modelo, Pedido... copias) {
        for (Pedido copia : copias) {
            for (ItemPedido item : copia.getItens()) {
                for (ItemPedido original : modelo.getItens()) {
                    if (item == original) return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Pedido modelo = new Pedido("(modelo)", "-");
        modelo.adicionarItem(new ItemPedido("Café 500g", 10, dinheiro("18.90")));
        modelo.adicionarItem(new ItemPedido("Açúcar 1kg", 20, dinheiro("4.50")));
        modelo.adicionarItem(new ItemPedido("Filtro de papel", 5, dinheiro("7.20")));

        CatalogoDePedidos catalogo = new CatalogoDePedidos();
        catalogo.registrar("reposicao-mensal", modelo);

        Pedido a = catalogo.criar("reposicao-mensal");
        a.setCliente("Mercado Central");
        a.setEnderecoEntrega("Rua das Flores, 120");

        Pedido b = catalogo.criar("reposicao-mensal");
        b.setCliente("Padaria Boa Massa");
        b.setEnderecoEntrega("Av. Paraná, 800");
        b.getItens().stream().filter(i -> i.getProduto().equals("Café 500g"))
                .findFirst().orElseThrow().setQuantidade(20);

        Pedido c = catalogo.criar("reposicao-mensal");
        c.setCliente("Restaurante do Porto");
        c.setEnderecoEntrega("Rua do Cais, 45");
        c.adicionarItem(new ItemPedido("Adoçante 100ml", 12, dinheiro("6.00")));

        System.out.println("=== PEDIDOS ===");
        System.out.println(modelo);
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println("\n=== CRITÉRIOS DE ACEITAÇÃO ===");
        Set<Long> ids = new HashSet<>();
        ids.add(modelo.getId()); ids.add(a.getId()); ids.add(b.getId()); ids.add(c.getId());
        verificar("CA-01", "IDs diferentes", ids.size() == 4);
        verificar("CA-02", "Total do modelo preservado", igual(modelo, "315.00"));
        verificar("CA-03", "Totais A, B e C corretos", igual(a, "315.00") && igual(b, "504.00") && igual(c, "387.00"));
        verificar("CA-04", "Modelo permanece com 3 itens", modelo.getItens().size() == 3);
        verificar("CA-05", "Itens das cópias independentes", itensIndependentes(modelo, a, b, c));
        boolean excecaoCorreta = false;
        try { catalogo.criar("inexistente"); }
        catch (IllegalArgumentException e) { excecaoCorreta = e.getMessage().contains("inexistente"); }
        verificar("CA-06", "Chave inexistente lança exceção", excecaoCorreta);
        verificar("CA-07", "Cliente do modelo preservado", modelo.getCliente().equals("(modelo)"));

        Pedido serializado = modelo.clonarPorSerializacao();
        verificar("RF-12", "Serialização com cópia profunda e novo ID", serializado.getId() != modelo.getId()
                && itensIndependentes(modelo, serializado) && serializado.getCriadoEm().equals(java.time.LocalDate.now()));
        final int repeticoes = 100_000;
        long inicio = System.nanoTime();
        for (int i = 0; i < repeticoes; i++) modelo.clonar();
        double manualMs = (System.nanoTime() - inicio) / 1_000_000.0;
        inicio = System.nanoTime();
        for (int i = 0; i < repeticoes; i++) modelo.clonarPorSerializacao();
        double serialMs = (System.nanoTime() - inicio) / 1_000_000.0;
        System.out.printf(java.util.Locale.US, "\n100.000 cópias manuais: %.3f ms%n", manualMs);
        System.out.printf(java.util.Locale.US, "100.000 cópias por serialização: %.3f ms%n", serialMs);
    }
}
