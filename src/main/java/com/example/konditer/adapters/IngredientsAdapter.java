package com.example.konditer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.konditer.R;
import com.example.konditer.models.IngredientChild;
import com.example.konditer.models.IngredientGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IngredientsAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<IngredientGroup> groups;
    private final Map<IngredientGroup, List<IngredientChild>> childrenMap;
    private final Map<IngredientGroup, List<Boolean>> selectionMap = new HashMap<>();

    public IngredientsAdapter(Context context, List<IngredientGroup> groups, Map<IngredientGroup, List<IngredientChild>> childrenMap) {
        this.context = context;
        this.groups = groups;
        this.childrenMap = childrenMap;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<IngredientChild> children = childrenMap.get(groups.get(groupPosition));
        return (children != null) ? children.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(childrenMap.get(groups.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_group, parent, false);
        }
        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setText(getGroup(groupPosition).toString());

        // Задаём фиксированную минимальную высоту группы, чтобы видеть больше элементов
        convertView.setMinimumHeight(70); // Можете увеличить или уменьшить по своему усмотрению
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_checkbox, parent, false);
        }
        CheckBox chkListItem = convertView.findViewById(R.id.chkListItem);
        IngredientChild ingredientChild = (IngredientChild) getChild(groupPosition, childPosition);
        chkListItem.setText(ingredientChild.getChildName());

        List<Boolean> selectionsForGroup = selectionMap.computeIfAbsent(groups.get(groupPosition), k -> new ArrayList<>());

        while (selectionsForGroup.size() <= childPosition) {
            selectionsForGroup.add(false);
        }

        chkListItem.setChecked(selectionsForGroup.get(childPosition));
        chkListItem.setOnCheckedChangeListener((buttonView, isChecked) -> selectionsForGroup.set(childPosition, isChecked));

        convertView.setMinimumHeight(100); // Минимальная высота элемента
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // Возвращает список выбранных продуктов
    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList<>();
        for (IngredientGroup group : groups) {
            List<Boolean> selectionsForGroup = selectionMap.get(group);

            if (selectionsForGroup != null && !selectionsForGroup.isEmpty()) {
                List<IngredientChild> children = childrenMap.get(group);

                for (int i = 0; i < Math.min(selectionsForGroup.size(), Objects.requireNonNull(children).size()); i++) {
                    if (selectionsForGroup.get(i)) {
                        selectedItems.add(children.get(i).getChildName());
                    }
                }
            }
        }
        return selectedItems;
    }
}