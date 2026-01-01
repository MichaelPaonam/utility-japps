package example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }
    @GetMapping("/{requestedById}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedById) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedById);
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

        //above return statement same as
/*        if(cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }*/
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard() {
        return ResponseEntity.created(URI.create("/what/should/go/here")).build();
    }
}
