package softuniBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuniBlog.entity.ConfirmationToken;
import softuniBlog.repository.ConfirmationTokenRepository;
import softuniBlog.service.serviceInt.ConfirmationTokenServiceInt;

@Service
public class ConfirmationTokenService implements ConfirmationTokenServiceInt {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public ConfirmationToken save(ConfirmationToken confirmationToken) {
        return this.confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public ConfirmationToken findByConfirmationToken(String confirmationToken) {
        return this.confirmationTokenRepository.findByConfirmationToken(confirmationToken);
    }
}
