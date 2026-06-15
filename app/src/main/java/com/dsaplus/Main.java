package com.dsaplus;

import com.dsaplus.channel.CommunicationChannel;
import com.dsaplus.client.Client;
import com.dsaplus.common.ds.SplayNode;
import com.dsaplus.model.Movie;
import com.dsaplus.server.Server;
import com.dsaplus.server.ds.ListNode;
import com.dsaplus.util.Logger;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Logger.info("Main", "Inicializando Sistema DSA-PLUS...");

        // Server initialization
        Server server = new Server();
        server.loadInitialData();

        // Three pre-registered clients
        Client[] clients = {
            new Client("Alice"),
            new Client("Bob"),
            new Client("Charlie")
        };

        // Pre-load 50 movies into each client's cache
        for (Client client : clients) {
            preloadCache(client, server);
        }

        Client currentClient = clients[0];
        boolean isRunning = true;

        while (isRunning) {
            printMenu(currentClient.getName());
            System.out.print("Escolha uma opção: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine();
                final Client client = currentClient;

                switch (option) {
                    case 1:
                        interactiveSearch(scanner, client, server);
                        break;
                    case 2:
                        interactiveSearch(scanner, client, server, false);
                        break;
                    case 3:
                        interactiveSearchByTitle(scanner, server);
                        break;
                    case 4:
                        CategoryConsoleViewer.handleCategoryPagination(
                            scanner, server,
                            (id) -> executeQuery(client, server, id, true)
                        );
                        break;
                    case 5:
                        currentClient = selectClient(scanner, clients);
                        break;
                    case 6:
                        runTestBattery(clients, server);
                        break;
                    case 7:
                        showFinalAnalysis(clients, server);
                        break;
                    case 0:
                        Logger.info("Main", "Encerrando a aplicação. Até logo!");
                        isRunning = false;
                        break;
                    default:
                        Logger.warn("Main", "Opção inválida.");
                }
            } catch (InputMismatchException e) {
                Logger.warn("Main", "Por favor, digite apenas números.");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    // --- INTERFACE ---

    private static void printMenu(String clientName) {
        System.out.println("\n==================================================");
        System.out.println("                    DSA-PLUS");
        System.out.println("            Cliente ativo: " + clientName);
        System.out.println("==================================================");
        System.out.println("1. Buscar um Filme por ID");
        System.out.println("2. Buscar um Filme por ID (SEM Índice)");
        System.out.println("3. Buscar Filme por Trecho do Nome");
        System.out.println("4. Listar e Paginar por Categoria");
        System.out.println("5. Trocar de Cliente");
        System.out.println("6. Executar Bateria de Consultas (3 clientes)");
        System.out.println("7. Exibir Análise Final");
        System.out.println("0. Sair");
        System.out.println("==================================================");
    }

    private static Client selectClient(Scanner scanner, Client[] clients) {
        System.out.println("\n--- Selecione um cliente ---");
        for (int i = 0; i < clients.length; i++) {
            System.out.println((i + 1) + ". " + clients[i].getName());
        }
        System.out.print("Escolha (1-" + clients.length + "): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice >= 1 && choice <= clients.length) {
                Client chosen = clients[choice - 1];
                Logger.info("Main", "Cliente ativo: " + chosen.getName());
                return chosen;
            }
        } catch (InputMismatchException e) {
            scanner.nextLine();
        }
        Logger.warn("Main", "Opção inválida. Mantendo cliente atual.");
        return clients[0];
    }

    // --- BUSCAS INTERATIVAS ---

    private static void interactiveSearch(Scanner scanner, Client client, Server server) {
        interactiveSearch(scanner, client, server, true);
    }

    private static void interactiveSearch(Scanner scanner, Client client, Server server, boolean useIndex) {
        System.out.print("\nDigite o ID do filme que deseja assistir (1 a 1000): ");
        try {
            int targetId = scanner.nextInt();
            executeQuery(client, server, targetId, useIndex);
        } catch (InputMismatchException e) {
            Logger.warn("Main", "ID inválido. Digite um número inteiro.");
            scanner.nextLine();
        }
    }

    private static void interactiveSearchByTitle(Scanner scanner, Server server) {
        System.out.print("\nDigite um trecho do título do filme: ");
        String fragment = scanner.nextLine();

        List<Movie> results = server.requestMoviesByTitle(fragment);

        if (results == null || results.isEmpty()) {
            Logger.info("Main", "Nenhum filme encontrado com o trecho: \"" + fragment + "\"");
        } else {
            System.out.println("Filmes encontrados (" + results.size() + "):");
            for (Movie m : results) {
                System.out.println(" - " + m);
            }
        }
    }

    // --- PRÉ-CARREGAMENTO ---

    private static void preloadCache(Client client, Server server) {
        ListNode current = server.getDatabase().getHead();
        int count = 0;

        while (current != null && count < 50) {
            client.getCache().put(current.getMovie().getId(), current.getMovie());
            current = current.getNext();
            count++;
        }
        Logger.info(client.getName(), "Cache inicializado com " + count + " filmes");
    }

    // --- BATERIA DE CONSULTAS ---

    private static void runTestBattery(Client[] clients, Server server) {
        Logger.setLevel(Logger.Level.INFO);

        System.out.println("\n========== BATERIA DE CONSULTAS (3 CLIENTES) ==========\n");

        for (Client client : clients) {
            System.out.println(">>> " + client.getName() + " <<<");

            System.out.println("  Etapa 1: 2 Consultas Inválidas");
            executeQuery(client, server, -5, true);
            executeQuery(client, server, 0, true);

            System.out.println("  Etapa 2: 6 Consultas no Cache (Hit)");
            int count = 0;
            ListNode current = server.getDatabase().getHead();
            while (current != null && count < 6) {
                executeQuery(client, server, current.getMovie().getId(), true);
                current = current.getNext();
                count++;
            }

            System.out.println("  Etapa 3: 6 Consultas SEM Indexação (Miss)");
            int[] slowIds = {99901, 99902, 99903, 38055, 575264, 813};
            for (int id : slowIds) executeQuery(client, server, id, false);

            System.out.println("  Etapa 4: 6 Consultas COM Indexação (Miss)");
            int[] fastIds = {88801, 88802, 88803, 140300, 810693, 524434};
            for (int id : fastIds) executeQuery(client, server, id, true);
        }

        Logger.info("Main", "Bateria de consultas concluída!");
        showFinalAnalysis(clients, server);
    }

    private static void executeQuery(Client client, Server server, int searchId, boolean useIndex) {
        Logger.info("Main", "Consulta: " + client.getName() + " ID " + searchId + (useIndex ? " (COM índice)" : " (SEM índice)"));

        Movie movie = client.getCache().get(searchId);

        if (movie == null) {
            if (useIndex) {
                movie = server.requestMovieWithIndex(searchId);
            } else {
                movie = server.requestMovieWithoutIndex(searchId);
            }
        }

        client.viewMovie(movie);
    }

    // --- ANÁLISE FINAL ---

    private static void showFinalAnalysis(Client[] clients, Server server) {
        System.out.println("\n==================================================");
        System.out.println("               ANÁLISE DOS RESULTADOS             ");
        System.out.println("==================================================\n");

        // 1. LRU Cache - Top 10 mais recentes por cliente
        System.out.println("--- 1. TOP 10 FILMES MAIS RECENTES (LRU) POR CLIENTE ---\n");
        for (Client client : clients) {
            System.out.println(">>> " + client.getName() + ":");
            List<Movie> recent = client.getRecentMovies(10);
            if (recent.isEmpty()) {
                System.out.println("  (Nenhum filme no cache)\n");
            } else {
                for (int i = 0; i < recent.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + recent.get(i));
                }
                System.out.println();
            }

            // IDs removidos pelo LRU
            List<Integer> evicted = client.getEvictionHistory();
            if (!evicted.isEmpty()) {
                System.out.println("  IDs removidos pelo LRU: " + evicted + "\n");
            }
        }

        // 2. Splay de Preferências - Top 5 por cliente
        System.out.println("--- 2. ÁRVORE SPLAY DE PREFERÊNCIAS (TOP 5 POR CLIENTE) ---\n");
        for (Client client : clients) {
            System.out.println(">>> " + client.getName() + ":");
            List<SplayNode> topPrefs = client.getPreferences().getTop(5);
            if (topPrefs.isEmpty()) {
                System.out.println("  (Nenhuma preferência registrada)\n");
            } else {
                for (int i = 0; i < topPrefs.size(); i++) {
                    Movie m = topPrefs.get(i).getValue();
                    if (m != null) {
                        System.out.println("  " + (i + 1) + ". [ID: " + m.getId() + "] " + m.getTitle());
                    }
                }
                System.out.println();
            }

            // Recomendação baseada na raiz
            String category = client.getRecommendation();
            if (category != null) {
                System.out.println("  -> Recomendação: filmes da categoria \"" + category + "\"\n");
            }
        }

        // 3. Splay de Popularidade do Servidor - Top 10
        System.out.println("--- 3. ÁRVORE SPLAY DE POPULARIDADE DO SERVIDOR (TOP 10) ---\n");
        List<Movie> topPopular = server.getTopMovies(10);
        if (topPopular.isEmpty()) {
            System.out.println("  (Nenhum filme acessado)\n");
        } else {
            for (int i = 0; i < topPopular.size(); i++) {
                Movie m = topPopular.get(i);
                System.out.println("  " + (i + 1) + ". [ID: " + m.getId() + "] " + m.getTitle() + " (" + m.getCategory() + ")");
            }
            System.out.println();
        }

        // 4. Huffman Compression Stats
        System.out.println("--- 4. COMPRESSÃO DE HUFFMAN (MENSAGENS SELECIONADAS) ---\n");
        String[] messages = {
            "LOGIN_OK",
            "GET /filme/505",
            "GET /filme/101",
            "FILME: 505 | Matrix 1999",
            "FILME: 101 | Avatar 2009",
            "RECOMENDACAO: 202 | Interestelar",
            "RECOMENDACAO: 603 | Matrix 1999",
        };
        for (String msg : messages) {
            System.out.println("  Mensagem: \"" + msg + "\"");
            System.out.println("  " + HuffmanCoding.compressionStats(msg));
            System.out.println();
        }

        System.out.println("==================================================\n");
    }
}
