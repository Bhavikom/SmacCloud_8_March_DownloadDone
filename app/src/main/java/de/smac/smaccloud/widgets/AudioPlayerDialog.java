package de.smac.smaccloud.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;

import de.smac.smaccloud.R;

public class AudioPlayerDialog extends AlertDialog
{
    MediaPlayer player;
    View content;
    ImageView btn_play_pause_music;
    AppCompatSeekBar seekBarMusic;
    ImageView btn_close_music;
    Handler seekHandler;
    Runnable runMusic;
    private Context context;


    public AudioPlayerDialog(Context context, int fileId)
    {

        super(context);
        this.context = context;
        this.setCancelable(true);

        File audioFile = new File("" + context.getFilesDir() + "/" + fileId);
        player = MediaPlayer.create(context, Uri.fromFile(audioFile));

        LayoutInflater li = LayoutInflater.from(context);
        content = li.inflate(R.layout.dialog_audio_player, null);
        setView(content);
        btn_play_pause_music = (ImageView) content.findViewById(R.id.btn_play_pause_music);
        seekBarMusic = (AppCompatSeekBar) content.findViewById(R.id.seekbarMusic);
        btn_close_music = (ImageView) content.findViewById(R.id.btn_close_music);
        btn_close_music.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                player.stop();
                dismiss();
            }
        });
        seekBarMusic.setMax(player.getDuration());
        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    player.seekTo(progress);
                    seekBar.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        seekHandler = new Handler();
        runMusic = new Runnable()
        {
            @Override
            public void run()
            {
                if (player != null && player.isPlaying())
                {
                    if (player.getCurrentPosition() < player.getDuration())
                    {
                        seekBarMusic.setProgress(player.getCurrentPosition());
                        seekHandler.postDelayed(this, 100);
                    }
                    else
                    {
                        if (player.isPlaying())
                        {
                            player.pause();
                            seekBarMusic.setProgress(0);
                            btn_play_pause_music.setImageResource(R.drawable.ic_play);

                        }
                    }
                }
                /*else if (player != null && seekBarMusic.getProgress() <= player.getDuration())
                {
                    player.pause();
                    seekBarMusic.setProgress(0);
                    btn_play_pause_music.setImageResource(R.drawable.ic_play);
                }*/
            }
        };

        seekHandler.postDelayed(runMusic, 100);
        if (!player.isPlaying())
        {
            player.start();
            btn_play_pause_music.setImageResource(R.drawable.ic_pause);
        }

        btn_play_pause_music.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (player.isPlaying())
                {
                    player.pause();
                    btn_play_pause_music.setImageResource(R.drawable.ic_play);
                }
                else
                {
                    player.start();
                    btn_play_pause_music.setImageResource(R.drawable.ic_pause);
                    seekHandler.postDelayed(runMusic, 100);
                }
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                player.pause();
                seekBarMusic.setProgress(0);
                btn_play_pause_music.setImageResource(R.drawable.ic_play);
            }
        });

        this.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                if (player.isPlaying())
                {
                    player.stop();
                }
            }
        });
    }
}
