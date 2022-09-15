package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll
    void start() {
        usuarioRepository.deleteAll();

        usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", " "));
    }
        @Test
        @DisplayName("Cadastrar Um usuário")
        public void deveCriarUmUsuario(){

            HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Vitoria dos santos","vivi@gmail.com","45678123","http://img.com/vicvi01.jpg"));

            ResponseEntity<Usuario> corpoResposta = testRestTemplate
                    .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

            assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
            assertEquals(corpoRequisicao.getBody().getNome(),corpoResposta.getBody().getNome());
            assertEquals(corpoRequisicao.getBody().getUsuario(),corpoResposta.getBody().getUsuario());

        }

        @Test
        @DisplayName("Não deve permitir duplicação do Usuário")
        public void naoDeveDuplicarUsuario(){

        usuarioService.cadastrarUsuario(new Usuario(0L,"Maria da silva","maria@gmail.com","87654321","http://img.com/maria50.jpg"));

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,"Maria da silva","maria@gmail.com","87654321","http://img.com/maria50.jpg"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar",HttpMethod.POST, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
        }

        @Test
        @DisplayName("Atualizar um Usuário")
        public void deveAtualizarUmUsuario(){

            Optional<Usuario> usuariocadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,"Juliana Damaceno", "ju_maceno@email.com","juliana321", "https://ju.img.com/hkhn.jpg"));

            Usuario usuarioUpdate = new Usuario(usuariocadastrado.get().getId(),"Juliana Damaceno","juliana_dama@email.com", "juliana321","https://ju.img.com/hkhn.jpg");

            HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

            ResponseEntity<Usuario> corpoResposta = testRestTemplate
                    .withBasicAuth("root@root.com", "rootroot")
                    .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

            assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
            assertEquals(corpoRequisicao.getBody().getNome(),corpoResposta.getBody().getNome());
            assertEquals(corpoRequisicao.getBody().getUsuario(),corpoResposta.getBody().getUsuario());

        }

        @Test
        @DisplayName("Listar todos usuarios")
        public void deveMostrarTodosUsuarios(){

        usuarioService.cadastrarUsuario(new Usuario(0L,"Luisa Soares","lu_soares@email.com","lulu123", "https://i.img.com/lulu90s.jpg"));
        usuarioService.cadastrarUsuario(new Usuario(0L,"Ricardo Marques", "rick@email.com","rick123","https://rick.img/dsff.jpg"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/all",HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        }


    }
