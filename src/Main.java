
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

// 1. Classe de modelo (entidade)
class Cliente {

    private int id;
    private String nome;
    private String email;

    public Cliente(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + " | Nome: " + nome
                + " | Email: " + email;
    }
}

// 2. Classe principal com interface gráfica Swing
public class Main extends JFrame {

    // Cores da interface
    private static final Color FUNDO = new Color(13, 17, 23);
    private static final Color PAINEL = new Color(22, 29, 39);
    private static final Color CAMPO = new Color(30, 39, 52);
    private static final Color CIANO = new Color(45, 212, 191);
    private static final Color BRANCO = new Color(235, 241, 248);
    private static final Color CINZA = new Color(155, 170, 188);
    private static final Color VERMELHO = new Color(239, 68, 68);
    private static final Color AMARELO = new Color(245, 158, 11);

    // Dados em memória
    private final List<Cliente> bancoDeDados = new ArrayList<>();
    private int proximoId = 1;

    // Componentes da interface
    private final JTextField campoNome = new JTextField(22);
    private final JTextField campoEmail = new JTextField(22);
    private final JTextField campoBusca = new JTextField(22);

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final TableRowSorter<DefaultTableModel> ordenador;

    private final JLabel labelTotal = new JLabel("Total de clientes: 0");
    private final JLabel labelFormulario = new JLabel("Cadastrar novo cliente");

    private final JButton botaoCadastrar = new JButton("Cadastrar");
    private final JButton botaoAtualizar = new JButton("Atualizar");
    private final JButton botaoExcluir = new JButton("Excluir");
    private final JButton botaoLimpar = new JButton("Limpar campos");

    // ID do cliente selecionado para edição ou exclusão
    private Integer idSelecionado = null;

    public Main() {
        super("CRUD de Clientes");

        // Configuração da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        getContentPane().setBackground(FUNDO);
        setLayout(new BorderLayout(18, 18));

        // Dados iniciais de exemplo
        cadastrarInicial("Ana Silva", "ana@email.com");
        cadastrarInicial("Bruno Souza", "bruno@email.com");
        cadastrarInicial("Carlos Oliveira", "carlos@email.com");

        // Modelo da tabela
        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        ordenador = new TableRowSorter<>(modeloTabela);
        tabela.setRowSorter(ordenador);

        configurarTabela();
        atualizarTabela();

        // Montagem da tela
        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);

        // Eventos
        configurarEventos();

        // Estado inicial
        atualizarBotoes();
    }

    // Cadastra os clientes fictícios iniciais
    private void cadastrarInicial(String nome, String email) {
        bancoDeDados.add(new Cliente(proximoId++, nome, email));
    }

    // Cabeçalho da aplicação
    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(PAINEL);
        painel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel titulo = new JLabel("CRUD DE CLIENTES");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(CIANO);

        JLabel subtitulo = new JLabel(
                "Gerencie seus clientes de forma simples e organizada."
        );
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(CINZA);

        JPanel textos = new JPanel(new BorderLayout(0, 6));
        textos.setBackground(PAINEL);
        textos.add(titulo, BorderLayout.NORTH);
        textos.add(subtitulo, BorderLayout.SOUTH);

        painel.add(textos, BorderLayout.CENTER);

        return painel;
    }

    // Conteúdo principal: tabela e formulário
    private JPanel criarConteudo() {
        JPanel principal = new JPanel(new BorderLayout(18, 0));
        principal.setBackground(FUNDO);
        principal.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        principal.add(criarPainelTabela(), BorderLayout.CENTER);
        principal.add(criarPainelFormulario(), BorderLayout.EAST);

        return principal;
    }

    // Painel de listagem
    private JPanel criarPainelTabela() {
        JPanel painel = criarPainelBase();
        painel.setLayout(new BorderLayout(0, 14));

        JLabel titulo = criarTitulo("Lista de clientes");

        configurarCampo(campoBusca);
        campoBusca.setToolTipText("Buscar por nome ou email");

        JPanel painelBusca = new JPanel(new BorderLayout(0, 10));
        painelBusca.setBackground(PAINEL);
        painelBusca.add(titulo, BorderLayout.NORTH);
        painelBusca.add(campoBusca, BorderLayout.SOUTH);

        configurarTabela();

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(CAMPO));
        rolagem.getViewport().setBackground(PAINEL);

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(rolagem, BorderLayout.CENTER);

        return painel;
    }

    // Painel do formulário
    private JPanel criarPainelFormulario() {
        JPanel painel = criarPainelBase();
        painel.setPreferredSize(new Dimension(310, 0));
        painel.setLayout(new BorderLayout(0, 16));

        JLabel titulo = criarTitulo("Dados do cliente");

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(PAINEL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formulario.add(criarLabel("Nome completo"), gbc);

        gbc.gridy = 1;
        formulario.add(campoNome, gbc);
        configurarCampo(campoNome);
        campoNome.setToolTipText("Digite o nome do cliente");

        gbc.gridy = 2;
        formulario.add(criarLabel("Email"), gbc);

        gbc.gridy = 3;
        formulario.add(campoEmail, gbc);
        configurarCampo(campoEmail);
        campoEmail.setToolTipText("Digite o email do cliente");

        JPanel botoes = new JPanel(new GridBagLayout());
        botoes.setBackground(PAINEL);

        GridBagConstraints gbcBotao = new GridBagConstraints();
        gbcBotao.gridx = 0;
        gbcBotao.weightx = 1;
        gbcBotao.fill = GridBagConstraints.HORIZONTAL;
        gbcBotao.insets = new Insets(5, 0, 5, 0);

        estilizarBotao(botaoCadastrar, CIANO, FUNDO);
        estilizarBotao(botaoAtualizar, new Color(59, 130, 246), Color.WHITE);
        estilizarBotao(botaoExcluir, VERMELHO, Color.WHITE);
        estilizarBotao(botaoLimpar, CAMPO, BRANCO);

        gbcBotao.gridy = 0;
        botoes.add(botaoCadastrar, gbcBotao);

        gbcBotao.gridy = 1;
        botoes.add(botaoAtualizar, gbcBotao);

        gbcBotao.gridy = 2;
        botoes.add(botaoExcluir, gbcBotao);

        gbcBotao.gridy = 3;
        botoes.add(botaoLimpar, gbcBotao);

        JPanel parteSuperior = new JPanel(new BorderLayout(0, 15));
        parteSuperior.setBackground(PAINEL);
        parteSuperior.add(titulo, BorderLayout.NORTH);
        parteSuperior.add(formulario, BorderLayout.CENTER);

        painel.add(parteSuperior, BorderLayout.NORTH);
        painel.add(botoes, BorderLayout.CENTER);

        return painel;
    }

    // Rodapé com total de clientes
    private JPanel criarRodape() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(0, 22, 14, 22));

        labelTotal.setForeground(CINZA);
        labelTotal.setFont(new Font("SansSerif", Font.PLAIN, 12));

        painel.add(labelTotal, BorderLayout.WEST);

        return painel;
    }

    // Configuração visual da tabela
    private void configurarTabela() {
        tabela.setBackground(PAINEL);
        tabela.setForeground(BRANCO);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setRowHeight(34);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(new Color(25, 100, 115));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setGridColor(CAMPO);
        tabela.setShowGrid(true);
        tabela.setFillsViewportHeight(true);

        tabela.getTableHeader().setBackground(CAMPO);
        tabela.getTableHeader().setForeground(CIANO);
        tabela.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tabela.getTableHeader().setReorderingAllowed(false);
    }

    // Cria um painel com estilo padrão
    private JPanel criarPainelBase() {
        JPanel painel = new JPanel();
        painel.setBackground(PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CAMPO),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        return painel;
    }

    private JLabel criarTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        label.setForeground(BRANCO);
        return label;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(CINZA);
        return label;
    }

    private void configurarCampo(JTextField campo) {
        campo.setBackground(CAMPO);
        campo.setForeground(BRANCO);
        campo.setCaretColor(CIANO);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 70, 88)),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)
        ));
    }

    private void estilizarBotao(
            JButton botao, Color fundo, Color texto
    ) {
        botao.setBackground(fundo);
        botao.setForeground(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 12));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(0, 38));
    }

    // Configura as ações dos componentes
    private void configurarEventos() {

        botaoCadastrar.addActionListener(e -> cadastrarCliente());

        botaoAtualizar.addActionListener(e -> atualizarCliente());

        botaoExcluir.addActionListener(e -> deletarCliente());

        botaoLimpar.addActionListener(e -> limparCampos());

        // Ao selecionar uma linha, carrega os dados no formulário
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarClienteSelecionado();
            }
        });

        // Pesquisa automática por nome ou email
        campoBusca.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filtrarTabela();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filtrarTabela();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filtrarTabela();
                    }
                }
        );
    }

    // C - CREATE
    private void cadastrarCliente() {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();

        if (!validarCampos(nome, email)) {
            return;
        }

        Cliente novoCliente = new Cliente(proximoId++, nome, email);
        bancoDeDados.add(novoCliente);

        atualizarTabela();
        limparCampos();

        JOptionPane.showMessageDialog(
                this,
                "Cliente cadastrado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // R - READ
    private void listarClientes() {
        atualizarTabela();
    }

    // U - UPDATE
    private void atualizarCliente() {
        if (idSelecionado == null) {
            mostrarAviso("Selecione um cliente na tabela para atualizar.");
            return;
        }

        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();

        if (!validarCampos(nome, email)) {
            return;
        }

        Optional<Cliente> encontrado = buscarPorId(idSelecionado);

        if (encontrado.isPresent()) {
            Cliente cliente = encontrado.get();
            cliente.setNome(nome);
            cliente.setEmail(email);

            atualizarTabela();
            limparCampos();

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            mostrarErro("Cliente não encontrado.");
            atualizarTabela();
        }
    }

    // D - DELETE
    private void deletarCliente() {
        if (idSelecionado == null) {
            mostrarAviso("Selecione um cliente na tabela para excluir.");
            return;
        }

        Optional<Cliente> encontrado = buscarPorId(idSelecionado);

        if (!encontrado.isPresent()) {
            mostrarErro("Cliente não encontrado.");
            atualizarTabela();
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o cliente "
                        + encontrado.get().getNome() + "?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resposta == JOptionPane.YES_OPTION) {
            bancoDeDados.remove(encontrado.get());

            atualizarTabela();
            limparCampos();

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente excluído com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // Busca um cliente pelo ID
    private Optional<Cliente> buscarPorId(int id) {
        return bancoDeDados.stream()
                .filter(cliente -> cliente.getId() == id)
                .findFirst();
    }

    // Valida os dados do formulário
    private boolean validarCampos(String nome, String email) {
        if (nome.isEmpty() || email.isEmpty()) {
            mostrarAviso("Preencha todos os campos antes de continuar.");
            return false;
        }

        if (nome.length() < 2) {
            mostrarAviso("O nome deve ter pelo menos 2 caracteres.");
            return false;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            mostrarAviso("Digite um endereço de email válido.");
            return false;
        }

        return true;
    }

    // Atualiza as linhas da tabela
    private void atualizarTabela() {
        modeloTabela.setRowCount(0);

        for (Cliente cliente : bancoDeDados) {
            modeloTabela.addRow(new Object[]{
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getEmail()
            });
        }

        labelTotal.setText(
                "Total de clientes: " + bancoDeDados.size()
        );

        tabela.clearSelection();
    }

    // Carrega os dados do cliente selecionado nos campos
    private void carregarClienteSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada == -1) {
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linhaSelecionada);
        int id = (int) modeloTabela.getValueAt(linhaModelo, 0);

        Optional<Cliente> encontrado = buscarPorId(id);

        if (encontrado.isPresent()) {
            Cliente cliente = encontrado.get();

            idSelecionado = cliente.getId();
            campoNome.setText(cliente.getNome());
            campoEmail.setText(cliente.getEmail());
        }

        atualizarBotoes();
    }

    // Pesquisa por nome ou email
    private void filtrarTabela() {
        String texto = campoBusca.getText().trim();

        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + java.util.regex.Pattern.quote(texto),
                            1, 2
                    )
            );
        }
    }

    // Limpa os campos e desmarca a seleção
    private void limparCampos() {
        idSelecionado = null;

        campoNome.setText("");
        campoEmail.setText("");

        tabela.clearSelection();

        atualizarBotoes();
        campoNome.requestFocusInWindow();
    }

    // Habilita ou desabilita botões conforme a seleção
    private void atualizarBotoes() {
        boolean selecionado = idSelecionado != null;

        botaoCadastrar.setEnabled(!selecionado);
        botaoAtualizar.setEnabled(selecionado);
        botaoExcluir.setEnabled(selecionado);

        labelFormulario.setText(
                selecionado
                        ? "Editar cliente ID " + idSelecionado
                        : "Cadastrar novo cliente"
        );
    }

    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Atenção",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // Inicialização da aplicação
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println(
                        "Não foi possível aplicar o tema do sistema."
                );
            }

            Main janela = new Main();
            janela.setVisible(true);
        });
    }
}
