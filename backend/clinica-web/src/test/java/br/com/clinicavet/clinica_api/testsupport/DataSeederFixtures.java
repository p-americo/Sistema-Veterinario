package br.com.clinicavet.clinica_api.testsupport;

/**
 * Credenciais dos usuários populados por {@code DataSeeder} em ambiente de testes (perfil "dev").
 * Centralizado aqui para evitar divergência caso o seeder mude os valores.
 */
public final class DataSeederFixtures {

    public static final String ADMIN_LOGIN = "12345678900";
    public static final String ADMIN_SENHA = "Senha123!";

    public static final String CLIENTE_LOGIN = "11111111111";
    public static final String CLIENTE_SENHA = "Senha123!";

    private DataSeederFixtures() {
    }
}
