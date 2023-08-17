package br.dreambricks.heinekenquiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;  // Assuming you have a repository for Player objects

    @GetMapping
    public List<Player> getAllPlayers() {
        return this.playerService.getAll();
    }



    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("nomeBar") String nomeBar,
                             RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/uploadStatus";
        }

        try {
            byte[] bytes = file.getBytes();

            Player player = new Player();
            player.setFileName(file.getOriginalFilename()); // Set the original file name
            player.setNomeBar(nomeBar);

            Calendar calendar = Calendar.getInstance();

            // Subtraindo 3 horas
            calendar.add(Calendar.HOUR_OF_DAY, -3);

            Date date = calendar.getTime();

            player.setDataCadastro(date);

            player.setFileEncrypted(bytes);

            playerRepository.save(player);

            redirectAttributes.addFlashAttribute("message", "File uploaded and Player created successfully");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload file");
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/download/{playerId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String playerId) {
        Player player = playerRepository.findById(playerId).orElse(null);

        if (player == null || player.getFileEncrypted() == null) {
            // Se o jogador não for encontrado ou o conteúdo do arquivo estiver ausente,
            // retorne uma resposta 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            // Encode o nome do arquivo para lidar com caracteres especiais
            String encodedFileName = URLEncoder.encode("ss.png", "UTF-8");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + player.getFileName() + "\"");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(player.getFileEncrypted()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


}