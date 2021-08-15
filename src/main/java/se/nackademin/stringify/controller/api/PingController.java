package se.nackademin.stringify.controller.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.nackademin.stringify.util.DateUtil;

/***
 * Rest controller for pinging the server when it is idle.
 */
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://stringify-chat.netlify.app"})
public class PingController {

    @GetMapping("api/ping")
    @ApiOperation(
            value = "Wake up the server",
            notes = "Pings the sleeping server whenever client is used",
            response = String.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = ResponseEntity.class, message = "Returns Server started..."),
    })
    public ResponseEntity<String> pingServer() {
        return ResponseEntity.ok("Server started...  \n" + DateUtil.now());
    }
}
