package net.befriendme.api.rest.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import net.befriendme.api.dto.RequestDto;
import net.befriendme.api.mongo.dao.ProductDao;
import net.befriendme.api.util.CustomerUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/stripe")
@CrossOrigin(origins = "*")
public class PaymentController {

    //  String STRIPE_API_KEY = System.getenv().get("STRIPE_API_KEY");
    private String STRIPE_API_KEY = "####";

    @GetMapping("/status")
    public String paymentStatus() throws StripeException {
        return "ok";
    }

    @PostMapping("/checkout/hosted")
    public String hostedCheckout(@RequestBody RequestDto requestDTO) throws StripeException {

        Stripe.apiKey = STRIPE_API_KEY;
        String clientBaseURL = "http://localhost:8001/payment/stripe";

        // Start by finding an existing customer record from Stripe or creating a new one if needed
        Customer customer = CustomerUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());

        // Next, create a checkout session by adding the details of the checkout
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(clientBaseURL + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(clientBaseURL + "/failure");

        for (Product product : requestDTO.getItems()) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .putMetadata("app_id", product.getId())
                                                            .setName(product.getName())
                                                            .build()
                                            )
                                            .setCurrency(ProductDao.getProduct(product.getId()).getDefaultPriceObject().getCurrency())
                                            .setUnitAmountDecimal(ProductDao.getProduct(product.getId()).getDefaultPriceObject().getUnitAmountDecimal())
                                            .build())
                            .build());
        }

        Session session = Session.create(paramsBuilder.build());

        return session.getUrl();
    }
}
