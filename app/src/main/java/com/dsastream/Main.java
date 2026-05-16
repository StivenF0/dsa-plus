package com.dsastream;

import com.dsastream.client.CategoryConsoleViewer;
import com.dsastream.client.Client;
import com.dsastream.model.Movie;
import com.dsastream.server.Server;
import com.dsastream.server.ds.ListNode;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Inicializando Sistema DSA-STREAM...");

        // Client and Server initialization
        Server server = new Server();
        server.loadInitialData();

        Client client = new Client();
        preloadCache(client, server); // Load 50 movies into client's cache

        boolean isRunning = true;

        // Main loop for user interaction
        while (isRunning) {
            printMenu();
            System.out.print("Escolha uma opção: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine();

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
                        CategoryConsoleViewer.handleCategoryPagination(scanner, server, (id) -> executeQuery(client, server, id, true));
                        break;
                    case 5:
                        runTestBattery(client, server);
                        break;
                    case 6:
                        printAnalysis();
                        break;
                    case 0:
                        System.out.println("\n[SISTEMA] Encerrando a aplicação. Até logo!");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("\n[ERRO] Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n[ERRO] Por favor, digite apenas números.");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    // --- MÉTODOS DE INTERFACE ---

    private static void printMenu() {
        System.out.println("\n==================================================");
        System.out.println("                    DSA-STREAM");
        System.out.println("==================================================");
        System.out.println("1. Buscar um Filme por ID");
        System.out.println("2. Buscar um Filme por ID (SEM Índice)");
        System.out.println("3. Buscar Filme por Trecho do Nome");
        System.out.println("4. Listar e Paginar por Categoria");
        System.out.println("5. Executar Bateria de 20 Consultas");
        System.out.println("6. Ler Análise dos Resultados");
        System.out.println("0. Sair");
        System.out.println("==================================================");
    }

    // --- MÉTODOS DE LÓGICA E REQUISITOS ---

    private static void preloadCache(Client client, Server server) {
        System.out.println("[SISTEMA] Pré-carregando catálogo inicial no cache do Cliente...");
        // Modificado para pré-carregar os primeiros 50 itens que estão fisicamente na lista
        ListNode current = server.getDatabase().getHead();
        int count = 0;

        while (current != null && count < 50) {
            client.addToCache(current.getMovie());
            current = current.getNext();
            count++;
        }
        System.out.println("[SISTEMA] Cache inicializado com " + count + " filmes!");
    }

    private static void interactiveSearch(Scanner scanner, Client client, Server server) {
        interactiveSearch(scanner, client, server, true);
    }

    private static void interactiveSearch(Scanner scanner, Client client, Server server, boolean useIndex) {
        System.out.print("\nDigite o ID do filme que deseja assistir (1 a 1000): ");
        try {
            int targetId = scanner.nextInt();
            executeQuery(client, server, targetId, useIndex);
        } catch (InputMismatchException e) {
            System.out.println("[ERRO] ID inválido. Digite um número inteiro.");
            scanner.nextLine();
        }
    }

    private static void interactiveSearchByTitle(Scanner scanner, Server server) {
        System.out.print("\nDigite um trecho do título do filme: ");
        String fragment = scanner.nextLine();

        List<Movie> results = server.requestMoviesByTitle(fragment);

        if (results == null || results.isEmpty()) {
            System.out.println("\n[App] Nenhum filme encontrado com o trecho: \"" + fragment + "\"");
        } else {
            System.out.println("\n[App] Filmes encontrados (" + results.size() + "):");
            for (Movie m : results) {
                System.out.println(" - " + m);
            }
        }
    }

    private static void runTestBattery(Client client, Server server) {
        System.out.println("\n==================================================");
        System.out.println("       INICIANDO BATERIA DE 20 CONSULTAS          ");
        System.out.println("==================================================\n");

        System.out.println(">>> ETAPA 1: 2 Consultas Inválidas");
        executeQuery(client, server, -5, true);
        executeQuery(client, server, 0, true);

        System.out.println("\n>>> ETAPA 2: 6 Consultas de registros no Cache (Hits)");
        // Como agora temos IDs reais que não são sequenciais (ex: Matrix = 603)
        // Pegamos os IDs dos primeiros 6 itens da lista para testar o Hit
        int count = 0;
        ListNode current = server.getDatabase().getHead();
        while (current != null && count < 6) {
            executeQuery(client, server, current.getMovie().getId(), true);
            current = current.getNext();
            count++;
        }

        System.out.println("\n>>> ETAPA 3: 6 Consultas SEM Indexação (Miss Lento)");
        // IDs fictícios (3 que não existem no servidor e 3 que existem mas não estão no cache)
        int[] slowIds = {99901, 99902, 99903, 38055, 575264, 813};
        for (int id : slowIds) executeQuery(client, server, id, false);

        System.out.println("\n>>> ETAPA 4: 6 Consultas COM Indexação (Miss Rápido)");
        // IDs fictícios (3 que não existem no servidor e 3 que existem mas não estão no cache)
        int[] fastIds = {88801, 88802, 88803, 140300, 810693, 524434};
        for (int id : fastIds) executeQuery(client, server, id, true);

        System.out.println("\n[SISTEMA] Bateria de testes concluída!");
    }

    private static void executeQuery(Client client, Server server, int searchId, boolean useIndex) {
        System.out.println("\n-> Buscando ID: " + searchId);

        // Tenta no Cache (Cliente)
        Movie movie = client.getCacheTree().search(searchId);

        if (movie == null) {
            // Tenta no Servidor (Miss)
            if (useIndex) {
                movie = server.requestMovieWithIndex(searchId);
            } else {
                movie = server.requestMovieWithoutIndex(searchId);
            }
        }

        client.viewMovie(movie);
    }

    private static void printAnalysis() {
        System.out.println("\n=== BREVE ANÁLISE DOS RESULTADOS ===");
        System.out.println("1. Cache (AVL): Eficiência O(log n). Buscas resultaram em ~5 comparações.");
        System.out.println("2. Servidor SEM Índice: Ineficiente O(n). Buscas resultaram em centenas de comparações.");
        System.out.println("3. Servidor COM Índice: Eficiência O(1). Tabela Hash resolveu com apenas 1 ou 2 comparações.");
        System.out.println("--------------------------------------------------");
    }
}