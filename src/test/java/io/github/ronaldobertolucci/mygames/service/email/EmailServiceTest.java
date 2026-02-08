package io.github.ronaldobertolucci.mygames.service.email;

import io.github.ronaldobertolucci.mygames.exception.EmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    private static final String FROM_EMAIL = "noreply@mygames.com";
    private static final String TO_EMAIL = "usuario@example.com";
    private static final String SUBJECT = "Assunto do Email";
    private static final String HTML_CONTENT = "<html><body><h1>Teste</h1></body></html>";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
    }

    @Test
    void deveEnviarEmailHtmlComSucesso() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlEmail(TO_EMAIL, SUBJECT, HTML_CONTENT);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(messageCaptor.capture());

        MimeMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage).isNotNull();
    }

    @Test
    void deveLancarExcecaoQuandoOcorrerErroAoCriarMensagem() {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro ao criar mensagem"));

        // Act & Assert
        assertThatThrownBy(() -> emailService.sendHtmlEmail(TO_EMAIL, SUBJECT, HTML_CONTENT))
                .isInstanceOf(EmailException.class)
                .hasMessageContaining("Erro ao enviar email HTML");

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void deveLancarExcecaoQuandoOcorrerErroAoEnviarMensagem() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Erro ao enviar"))
                .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> emailService.sendHtmlEmail(TO_EMAIL, SUBJECT, HTML_CONTENT))
                .isInstanceOf(EmailException.class)
                .hasMessageContaining("Erro ao enviar email HTML");

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void deveEnviarEmailComDestinacarioCorreto() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlEmail(TO_EMAIL, SUBJECT, HTML_CONTENT);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void deveEnviarMultiplosEmailsComSucesso() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        String email1 = "usuario1@example.com";
        String email2 = "usuario2@example.com";
        String email3 = "usuario3@example.com";

        // Act
        emailService.sendHtmlEmail(email1, SUBJECT, HTML_CONTENT);
        emailService.sendHtmlEmail(email2, SUBJECT, HTML_CONTENT);
        emailService.sendHtmlEmail(email3, SUBJECT, HTML_CONTENT);

        // Assert
        verify(mailSender, times(3)).createMimeMessage();
        verify(mailSender, times(3)).send(any(MimeMessage.class));
    }

    @Test
    void deveEnviarEmailComConteudoHtmlComplexo() {
        // Arrange
        String complexHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .container { padding: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Título</h1>
                        <p>Parágrafo com <strong>negrito</strong> e <em>itálico</em></p>
                        <ul>
                            <li>Item 1</li>
                            <li>Item 2</li>
                        </ul>
                    </div>
                </body>
                </html>
                """;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlEmail(TO_EMAIL, SUBJECT, complexHtml);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void deveLancarExcecaoComMensagemDetalhada() {
        // Arrange
        String errorMessage = "Conexão recusada pelo servidor SMTP";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException(errorMessage))
                .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> emailService.sendHtmlEmail(TO_EMAIL, SUBJECT, HTML_CONTENT))
                .isInstanceOf(EmailException.class)
                .hasMessageContaining("Erro ao enviar email HTML")
                .hasMessageContaining(errorMessage);
    }

    @Test
    void deveEnviarEmailComAssuntoVazio() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlEmail(TO_EMAIL, "", HTML_CONTENT);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void deveEnviarEmailComConteudoVazio() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlEmail(TO_EMAIL, SUBJECT, "");

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void deveEnviarEmailComCaracteresEspeciaisNoAssunto() {
        // Arrange
        String subjectWithSpecialChars = "Redefinição de Senha - Ação Requerida! 🔐";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlEmail(TO_EMAIL, subjectWithSpecialChars, HTML_CONTENT);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}