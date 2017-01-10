package jackszm.androiddevtweets;

import android.content.Context;

import java.util.List;

import jackszm.androiddevtweets.api.AccessTokenService;
import jackszm.androiddevtweets.domain.Tweet;
import jackszm.androiddevtweets.tweets.TweetsService;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

class MainActivityPresenter {

    private final TweetsDisplayer tweetsDisplayer;
    private final Scheduler subscribeOnScheduler;
    private final Scheduler observeOnScheduler;
    private final TweetsService tweetsService;

    static MainActivityPresenter newInstance(TweetsDisplayer tweetsDisplayer, Context context) {
        Scheduler subscribeOnScheduler = Schedulers.io();
        Scheduler observeOnScheduler = AndroidSchedulers.mainThread();
        AccessTokenService accessTokenService = AccessTokenService.newInstance(context);
        TweetsService tweetsService = TweetsService.newInstance(accessTokenService);
        return new MainActivityPresenter(
                tweetsDisplayer,
                tweetsService,
                subscribeOnScheduler,
                observeOnScheduler
        );
    }

    MainActivityPresenter(
            TweetsDisplayer tweetsDisplayer,
            TweetsService tweetsService,
            Scheduler subscribeOnScheduler,
            Scheduler observeOnScheduler
    ) {
        this.tweetsDisplayer = tweetsDisplayer;
        this.tweetsService = tweetsService;
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
    }

    void startPresenting() {
        loadTweets();
    }

    private void loadTweets() {
        tweetsService.loadTweets()
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .subscribe(displayTweets());
    }

    private Action1<List<Tweet>> displayTweets() {
        return new Action1<List<Tweet>>() {
            @Override
            public void call(List<Tweet> tweets) {
                tweetsDisplayer.displayTweets(tweets);
            }
        };
    }

    interface TweetsDisplayer {

        void displayTweets(List<Tweet> tweets);
    }
}
