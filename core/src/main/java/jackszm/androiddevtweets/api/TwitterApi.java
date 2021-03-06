package jackszm.androiddevtweets.api;

import rx.Observable;
import rx.functions.Func1;

public class TwitterApi {

    private static final String BASE_URL = "https://api.twitter.com/";

    private static final String URL_GET_ANDROID_DEV_TWEETS = "1.1/statuses/user_timeline.json?count=100&screen_name=androiddevRTbot";

    private final RequestExecutor requestExecutor;
    private final AuthenticationService authenticationService;
    private final AuthenticationInterceptor interceptor;

    public static TwitterApi newInstance(AuthenticationService authenticationService) {
        RequestExecutor requestExecutor = RequestExecutor.newInstance();
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authenticationService);
        return new TwitterApi(authenticationService, requestExecutor, interceptor);
    }

    TwitterApi(AuthenticationService authenticationService, RequestExecutor requestExecutor, AuthenticationInterceptor interceptor) {
        this.authenticationService = authenticationService;
        this.requestExecutor = requestExecutor;
        this.interceptor = interceptor;
    }

    public Observable<String> getAndroidDevTweets() {
        return authenticationService.retrieveAccessToken()
                .map(toRequest())
                .map(execute())
                .retryWhen(interceptor.retryRule());
    }

    private Func1<String, Request> toRequest() {
        return new Func1<String, Request>() {
            @Override
            public Request call(String accessToken) {
                return Request.builder(BASE_URL)
                        .path(URL_GET_ANDROID_DEV_TWEETS)
                        .bearerAuthorization(accessToken)
                        .build();
            }
        };
    }

    private Func1<Request, String> execute() {
        return new Func1<Request, String>() {
            @Override
            public String call(Request request) {
                return requestExecutor.executeRequest(request);
            }
        };
    }

}
