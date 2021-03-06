package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;


public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> implements TextToSpeech.OnInitListener{
    private ArrayList<Expense> expenseArrayList;
    private Context context;
    public static TextToSpeech mTTS;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenseArrayList){
        this.expenseArrayList = expenseArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return expenseArrayList.size();
    }

    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data,parent,false);
        ExpenseViewHolder expenseViewHolder = new ExpenseViewHolder(view);

        mTTS = new TextToSpeech(context.getApplicationContext(), this);

        expenseViewHolder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(parent.getContext(), ExpenseDetailsActivity.class);
            i.putExtra(ExpenseDetailsActivity.CATEGORY, expenseArrayList.get(expenseViewHolder.getAdapterPosition()).getCategory());
            i.putExtra(ExpenseDetailsActivity.TITLE, expenseArrayList.get(expenseViewHolder.getAdapterPosition()).getTitle());
            i.putExtra(ExpenseDetailsActivity.PRICE, expenseArrayList.get(expenseViewHolder.getAdapterPosition()).getPrice());
            String categ = expenseArrayList.get(expenseViewHolder.getAdapterPosition()).getCategory().trim();

            int catImg = 0;
            if (categ.equalsIgnoreCase("Utilities")){
                catImg = R.drawable.utilities;
            }
            else if(categ.equalsIgnoreCase("Food")){
                catImg = R.drawable.food;
            }
            else if(categ.equalsIgnoreCase("Transportation")) {
                catImg = R.drawable.transpo;
            }
            else if(categ.equalsIgnoreCase("Internet")) {
                catImg = R.drawable.internet;
            }
            else if(categ.equalsIgnoreCase("Home rent")) {
                catImg = R.drawable.homerent;
            }
            else if(categ.equalsIgnoreCase("Entertainment")) {
                catImg = R.drawable.entertainment;
            }
            else if(categ.equalsIgnoreCase("Gas")) {
                catImg = R.drawable.gas;
            }
            else if(categ.equalsIgnoreCase("Gift") ) {
                catImg = R.drawable.giftcard;
            }
            else if(categ.equalsIgnoreCase("Phone") ) {
                catImg = R.drawable.phone;
            }
            else if(categ.equalsIgnoreCase("Shopping")) {
                catImg = R.drawable.shopping;
            }

            i.putExtra(ExpenseDetailsActivity.CATEGORYPIC, catImg);
            i.putExtra(ExpenseDetailsActivity.RECEIPTIMG, expenseArrayList.get(expenseViewHolder.getAdapterPosition()).getReceiptID());
            String[] date = expenseArrayList.get(expenseViewHolder.getAdapterPosition()).getDateCreated().toDate().toString().split(" ");
            Log.i("Date Test", date[1] + " " + date[2] + " " + date[5] + " ----");
            i.putExtra(ExpenseDetailsActivity.DATEDET, date[1] + " " + date[2] + ", " + date[5]);
            i.putExtra("expenseID", expenseArrayList.get(expenseViewHolder.getAdapterPosition()).getExpenseID());
            i.putExtra("priceNoFormat",expenseArrayList.get(expenseViewHolder.getAdapterPosition()).getPrice());
            parent.getContext().startActivity(i);
        });

        return expenseViewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {

        holder.tv_expTitle.setText(expenseArrayList.get(position).getTitle());
        Picasso.with(context).load(expenseArrayList.get(position).getReceiptID()).fit().centerCrop().into(holder.iv_receipt);
        holder.tv_expPrice.setText(String.format("Php %.2f",expenseArrayList.get(position).getPrice()));
        String categ = expenseArrayList.get(holder.getAdapterPosition()).getCategory().trim();
        Log.i("Test", "I am hereeee");
        int catImg = 0;
        if (categ.equalsIgnoreCase("Utilities")){
            catImg = R.drawable.utilities;
        }
        else if(categ.equalsIgnoreCase("Food")){
            catImg = R.drawable.food;
            System.out.println("HELLOOOOO PUMASOK DITO HELLOOOO");
            Log.i("Test", categ);
        }
        else if(categ.equalsIgnoreCase("Transportation")) {
            catImg = R.drawable.transpo;
        }
        else if(categ.equalsIgnoreCase("Internet")) {
            catImg = R.drawable.internet;
        }
        else if(categ.equalsIgnoreCase("Home rent")) {
            catImg = R.drawable.homerent;
        }
        else if(categ.equalsIgnoreCase("Entertainment")) {
            catImg = R.drawable.entertainment;
        }
        else if(categ.equalsIgnoreCase("Gas")) {
            catImg = R.drawable.gas;
        }
        else if(categ.equalsIgnoreCase("Gift") ) {
            catImg = R.drawable.giftcard;
        }
        else if(categ.equalsIgnoreCase("Phone") ) {
            catImg = R.drawable.phone;
        }
        else if(categ.equalsIgnoreCase("Shopping")) {
            catImg = R.drawable.shopping;
        }
        holder.iv_category.setImageResource(catImg);

        holder.ib_tts.setOnClickListener(v -> {
            String title = expenseArrayList.get(position).getTitle();
            String price = expenseArrayList.get(position).getPrice().toString();

            String item = "You spent " +  price + " on " + title;

            mTTS.setSpeechRate(1f);
            mTTS.setPitch(1f);

            mTTS.speak(item , TextToSpeech.QUEUE_FLUSH, null , null);

        });
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            int result = mTTS.setLanguage(Locale.getDefault());

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("Text to Speech" , "Language not Supported");
            }
        } else {
            Log.e("Text to Speech" , "Initialize Failed");
        }
    }

    public void setData(ArrayList<Expense> data)
    {
        this.expenseArrayList.clear();
        this.expenseArrayList = data;
        notifyDataSetChanged();
    }

    protected class ExpenseViewHolder extends RecyclerView.ViewHolder{
        ImageButton ib_tts;
        ImageView iv_category;
        TextView tv_expTitle;
        TextView tv_expPrice;
        ImageView iv_receipt;

        public ExpenseViewHolder(View view){
           super(view);
           iv_category = view.findViewById(R.id.iv_category);
           iv_receipt = view.findViewById(R.id.iv_receipt);
           tv_expTitle = view.findViewById(R.id.tv_expTitle);
           tv_expPrice = view.findViewById(R.id.tv_expPrice);
           ib_tts = view.findViewById(R.id.ib_tts);
        }
    }


}
