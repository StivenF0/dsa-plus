package com.dsastream.client;

import com.dsastream.model.Movie;
import com.dsastream.server.Server;
import java.util.List;
import java.util.Scanner;

public class CategoryConsoleViewer {

    public static void handleCategoryPagination(Scanner scanner, Server server, MainMainExecutor queryExecutor) {
        System.out.print("\nDigite a categoria desejada (ex: Ação, Drama, Comédia): ");
        String category = scanner.nextLine().toLowerCase().trim();

        int totalMovies = server.getTotalMoviesInCategory(category);
        if (totalMovies == 0) {
            System.out.println("[App] Nenhuma obra encontrada para a categoria '" + category + "'.");
            return;
        }

        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) totalMovies / pageSize);
        int currentPage = 1;

        while (true) {
            List<Movie> pageMovies = server.requestMoviesByCategory(category, currentPage, pageSize);

            System.out.println("\n==================================================");
            System.out.println(" Categoria: " + category + " | Página " + currentPage + " de " + totalPages);
            System.out.println("==================================================");

            for (Movie m : pageMovies) {
                System.out.println(" [ID: " + m.getId() + "] " + m.getTitle());
            }
            System.out.println("==================================================");
            System.out.println(" [N] Próxima Página | [A] Página Anterior");
            System.out.println(" [ID] Assistir Filme | [S] Voltar ao Menu");
            System.out.print("Escolha uma ação: ");

            String action = scanner.nextLine().trim().toLowerCase();

            if (action.equals("s")) {
                break;
            } else if (action.equals("n")) {
                if (currentPage < totalPages) currentPage++;
                else System.out.println("[Aviso] Você já está na última página.");
            } else if (action.equals("a")) {
                if (currentPage > 1) currentPage--;
                else System.out.println("[Aviso] Você já está na primeira página.");
            } else {
                try {
                    int targetId = Integer.parseInt(action);
                    // Executa a busca através do fluxo principal (procura na AVL -> depois no Servidor)
                    queryExecutor.execute(targetId);
                } catch (NumberFormatException e) {
                    System.out.println("[ERRO] Comando ou ID inválido.");
                }
            }
        }
    }

    // Interface funcional para conectar com o executor do Main sem acoplamento circular
    @FunctionalInterface
    public interface MainMainExecutor {
        void execute(int id);
    }
}