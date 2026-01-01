package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }
    @GetMapping("/{requestedById}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedById, Principal principal) {

        Optional<CashCard> cashCardOptional =
                Optional.ofNullable(
                        cashCardRepository.findByIdAndOwner(requestedById, principal.getName())
                );
        /*Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedById);*/
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

        //above return statement same as
      /*if(cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }*/
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
//        return ResponseEntity.ok(cashCardRepository.findAll());
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(), // default is 0
                        pageable.getPageSize(), // default is 20
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest,
                                                UriComponentsBuilder ucb,
                                                Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null,
                newCashCardRequest.amount(),
                principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        URI locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        CashCard cashCard = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
        if(cashCard != null) {
            CashCard updateCashCard = new CashCard(
                                            cashCard.id(),
                                            cashCardUpdate.amount(),
                                            principal.getName());
            cashCardRepository.save(updateCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
