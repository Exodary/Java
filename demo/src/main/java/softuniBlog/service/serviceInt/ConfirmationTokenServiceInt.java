package softuniBlog.service.serviceInt;

import softuniBlog.entity.ConfirmationToken;

public interface ConfirmationTokenServiceInt {

    ConfirmationToken save(ConfirmationToken confirmationToken);

    ConfirmationToken findByConfirmationToken(String confirmationToken);
}
