package com.example.myadapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsDetailFragment extends Fragment {

    private static final String ARG_NEWS_ITEM = "ARG_NEWS_ITEM";

    private NewsItem newsItem;

    public static NewsDetailFragment newInstance(NewsItem newsItem) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS_ITEM, newsItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newsItem = (NewsItem) getArguments().getSerializable(ARG_NEWS_ITEM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.tvDetailTitle);
        TextView content = view.findViewById(R.id.tvDetailContent);
        TextView publicationDate = view.findViewById(R.id.tvPublicationDate);
        ImageView image = view.findViewById(R.id.ivDetailImage);

        if (newsItem != null) {
            title.setText(newsItem.getTitle());
            content.setText(newsItem.getContent());

            // Format and set the publication date
            if (newsItem.getPublicationDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM, yyyy", Locale.getDefault());
                publicationDate.setText(dateFormat.format(newsItem.getPublicationDate()));
            }

            // Use the large image for the detail view, with a fallback to the thumbnail
            String imageUrl = newsItem.getLargeImageUrl();
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = newsItem.getThumbUrl();
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Make image invisible before downloading
                image.setVisibility(View.INVISIBLE);
                
                // Download image and apply fade-in animation on complete
                new ImageDownloader().download(imageUrl, image, () -> {
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    image.startAnimation(fadeIn);
                    image.setVisibility(View.VISIBLE);
                });
            }

            // Animate the title using Animator
            animateTitle(title);
        }
    }

    private void animateTitle(View titleView) {
        // Set initial state (off-screen and transparent)
        titleView.setAlpha(0f);
        titleView.setTranslationX(-200f);

        // Create animators
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(titleView, "alpha", 0f, 1f);
        alphaAnimator.setDuration(600); // milliseconds

        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(titleView, "translationX", -200f, 0f);
        translationXAnimator.setDuration(600);

        // Play them together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnimator, translationXAnimator);
        animatorSet.start();
    }
}
