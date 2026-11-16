package com.nikolas.odteste.sigilos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nikolas.odteste.sigilos.RegrasEfeitoSigilo.Alvo;
import com.nikolas.odteste.sigilos.RegrasEfeitoSigilo.Efeito;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RegrasEfeitoSigiloTest {
    @Test
    void soOsCincoDeElementoTemEfeito() {
        int com = 0;
        for (String s : IdsSigilos.SIMBOLOS) {
            if (Efeito.deSimbolo(s).isPresent()) com++;
        }
        assertEquals(5, com);
        assertEquals(Efeito.MEDO, Efeito.deSimbolo("sigilo_de_medo").orElseThrow());
        assertTrue(Efeito.deSimbolo("sigilo_a").isEmpty());
        assertTrue(Efeito.deSimbolo("sigilo_de_invocacao").isEmpty());
        assertTrue(Efeito.deSimbolo(null).isEmpty());
    }

    @Test
    void sangueSoMachucaMonstro() {
        assertTrue(RegrasEfeitoSigilo.afeta(Efeito.SANGUE, Alvo.CRIATURA_HOSTIL, false));
        assertFalse(RegrasEfeitoSigilo.afeta(Efeito.SANGUE, Alvo.CRIATURA, false), "animal não é hostil");
        assertFalse(RegrasEfeitoSigilo.afeta(Efeito.SANGUE, Alvo.JOGADOR, false));
        assertFalse(RegrasEfeitoSigilo.afeta(Efeito.SANGUE, Alvo.QUEM_ATIVOU, false));
    }

    @Test
    void jogadorDoMesmoGrupoNaoEAfetadoExcetoNoMedo() {
        for (Efeito e : new Efeito[] {Efeito.MORTE, Efeito.CONHECIMENTO, Efeito.ENERGIA}) {
            assertTrue(RegrasEfeitoSigilo.afeta(e, Alvo.JOGADOR, false), e + " pega em jogador de fora do grupo");
            assertFalse(RegrasEfeitoSigilo.afeta(e, Alvo.JOGADOR, true), e + " poupa o mesmo grupo");
            assertFalse(RegrasEfeitoSigilo.afeta(e, Alvo.QUEM_ATIVOU, false), e + " não castiga quem ativou");
        }
        assertTrue(RegrasEfeitoSigilo.afeta(Efeito.MEDO, Alvo.JOGADOR, true), "o Medo não escolhe lado");
        assertTrue(RegrasEfeitoSigilo.afeta(Efeito.MEDO, Alvo.QUEM_ATIVOU, false), "nem poupa quem ativou");
    }

    @Test
    void criaturasEOutros() {
        assertTrue(RegrasEfeitoSigilo.afeta(Efeito.MORTE, Alvo.CRIATURA, false));
        assertTrue(RegrasEfeitoSigilo.afeta(Efeito.ENERGIA, Alvo.CRIATURA_HOSTIL, false));
        assertFalse(RegrasEfeitoSigilo.afeta(Efeito.MORTE, Alvo.OUTRO, false), "bicho com dono fica a salvo");
        assertTrue(RegrasEfeitoSigilo.afeta(Efeito.CONHECIMENTO, Alvo.OUTRO, false), "o brilho mostra até o suporte invisível");
        assertFalse(RegrasEfeitoSigilo.afeta(Efeito.MEDO, Alvo.OUTRO, false));
        assertFalse(RegrasEfeitoSigilo.afeta(null, Alvo.CRIATURA, false));
    }

    @Test
    void esperaTetoERaio() {
        assertTrue(RegrasEfeitoSigilo.livre(-1, 0, 600));
        assertFalse(RegrasEfeitoSigilo.livre(100, 699, 600));
        assertTrue(RegrasEfeitoSigilo.livre(100, 700, 600));
        assertTrue(RegrasEfeitoSigilo.livre(5000, 10, 600), "relógio voltou");
        assertTrue(RegrasEfeitoSigilo.cabeMais(47, 48));
        assertFalse(RegrasEfeitoSigilo.cabeMais(48, 48));
        assertTrue(RegrasEfeitoSigilo.cabeMais(0, 0), "teto nunca zera");
        assertEquals(RegrasEfeitoSigilo.RAIO_MAX, RegrasEfeitoSigilo.raio(500));
        assertEquals(1, RegrasEfeitoSigilo.raio(-3));
        assertEquals(5, RegrasEfeitoSigilo.RAIO_PADRAO);
        assertEquals(10, RegrasEfeitoSigilo.DURACAO_PADRAO_S);
        assertEquals(60, RegrasEfeitoSigilo.ATORDOAR_TICKS / 1, "3 s de atordoamento");
        assertEquals(221, RegrasEfeitoSigilo.duracaoVisaoNoturna(200));
        assertEquals(400, RegrasEfeitoSigilo.duracaoVisaoNoturna(400));
    }

    @Test
    void configEstaDocumentadaEOServidorNaoCarregaCliente() throws Exception {
        String ids = Files.readString(Path.of("docs/ids.md"));
        assertTrue(ids.contains(IdsSigilos.ARQUIVO_CONFIG), "falta " + IdsSigilos.ARQUIVO_CONFIG + " em docs/ids.md");
        String ativacao = Files.readString(Path.of("src/main/java/com/nikolas/odteste/sigilos/AtivacaoSigilo.java"));
        assertFalse(ativacao.contains("net.minecraft.client"), "ativação é só do servidor");
    }
}
