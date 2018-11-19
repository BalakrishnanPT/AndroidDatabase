package balakrishnan.me.sampledb;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ItemsRecyclerAdapter extends RealmRecyclerViewAdapter<Item, ItemsRecyclerAdapter.MyViewHolder> {

    public ItemsRecyclerAdapter(OrderedRealmCollection<Item> data) {
        super(data, true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Item item = getItem(position);
        holder.setItem(item);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        CheckBox checkBox;
        Item mItem;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.body);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(this);
        }

        void setItem(Item item) {
            this.mItem = item;
            this.textView.setText(item.getBody());
            this.checkBox.setChecked(item.getIsDone());
        }

        @Override
        public void onClick(View v) {
            String itemId = mItem.getItemId();
            boolean isDone = this.mItem.getIsDone();
            this.mItem.getRealm().executeTransactionAsync(realm -> {
                Item item = realm.where(Item.class).equalTo("itemId", itemId).findFirst();
                if (item != null) {
                    item.setIsDone(!isDone);
                }
            });
        }
    }
}
