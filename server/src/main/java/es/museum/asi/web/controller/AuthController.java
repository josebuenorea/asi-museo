package es.museum.asi.web.controller;

import es.museum.asi.model.exception.UserLoginExistsException;
import es.museum.asi.model.service.UserService;
import es.museum.asi.model.service.dto.UserDTOPrivate;
import es.museum.asi.web.controller.dto.RegisterRequest;
import es.museum.asi.web.util.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controlador de autenticación (HU1-HU3)
 * Endpoints públicos para registro, login y logout
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private UserService userService;

  /**
   * HU1 - Registro de nuevo usuario (VISITANTE)
   * POST /api/auth/register
   *
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    logger.info("Intento de registro:  login={}", request.getLogin());

    try {
      // Validar que las contraseñas coinciden
      if (!request.getPassword().equals(request.getPasswordConfirm())) {
        return ResponseEntity.badRequest()
          .body(new ErrorDTO("Las contraseñas no coinciden"));
      }

      // Registrar usuario (automáticamente VISITANTE)
      UserDTOPrivate user = userService.registerUser(request.getLogin(), request.getPassword());

      logger.info("Usuario '{}' registrado correctamente como VISITANTE", user.getLogin());

      return ResponseEntity.status(HttpStatus.CREATED).body(user);

    } catch (UserLoginExistsException e) {
      logger.warn("Error en registro: {}", e.getMessage());
      return ResponseEntity.badRequest()
        .body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      logger.error("Error inesperado en registro", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorDTO("Error en el registro"));
    }
  }

}
