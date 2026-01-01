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

@RestController
@RequestMapping("/cashcards")
class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }
    @GetMapping("/{requestedById}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedById, Principal principal) {
        CashCard cashCard = findCashCard(requestedById, principal);
        if(cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
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
        CashCard cashCard = findCashCard(requestedId, principal);
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
