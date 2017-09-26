package com.aganticsoft.phyxhidephotosandvideos.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.aganticsoft.phyxhidephotosandvideos.R;
import com.aganticsoft.phyxhidephotosandvideos.model.MediaModel;
import com.aganticsoft.phyxhidephotosandvideos.view.fragment.ChooseAlbumFragment;
import com.aganticsoft.phyxhidephotosandvideos.view.fragment.MediaPickerFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Two type of fragment inside: {@link ChooseAlbumFragment}, {@link MediaPickerFragment}
 */
public class MediaChooseActivity extends BaseActivity implements HasSupportFragmentInjector {

    private int albumType;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private ChooseAlbumFragment frgChooseAlbum;
    private MediaPickerFragment mediaPickerFragment;

    public static Intent getIntent(Context context, @MediaModel.MediaType int mediaType) {
        Intent intent = new Intent(context, MediaChooseActivity.class);
        intent.putExtra("type", mediaType);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_photo);
        ButterKnife.bind(this);

        albumType = getIntent().getIntExtra("type", MediaModel.MediaType.TYPE_IMAGE);

        initToolbar();
        initFragment();
    }

    private void initFragment() {
        frgChooseAlbum = ChooseAlbumFragment.getInstance(albumType);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, frgChooseAlbum)
                .addToBackStack(ChooseAlbumFragment.class.getSimpleName())
                .commit();
    }

    private void changeMode(boolean isChooseAlbum) {
        if (isChooseAlbum) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, frgChooseAlbum)
                    .addToBackStack(ChooseAlbumFragment.class.getSimpleName())
                    .commit();
        } else {
            if (mediaPickerFragment == null)
                return;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, mediaPickerFragment)
                    .addToBackStack(MediaPickerFragment.class.getSimpleName())
                    .commit();
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (albumType == MediaModel.MediaType.TYPE_IMAGE) {
            getSupportActionBar().setTitle("Choose images to hide");
        } else {
            getSupportActionBar().setTitle("Choose videos to hide");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
    }

    /**
     * When 1 album to be clicked and then we need to move {@link MediaPickerFragment}
     * to choose media items
     * <br/>
     * Get call from {@link ChooseAlbumFragment#onAlbumChooseClick(String, List)}
     * @param albumName Album Name
     * @param medias List of items in that album
     */
    public void onAlbumChooseClick(String albumName, List<MediaModel> medias) {
        mediaPickerFragment = MediaPickerFragment.newInstance(albumType, albumName, medias);

        changeMode(false);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
