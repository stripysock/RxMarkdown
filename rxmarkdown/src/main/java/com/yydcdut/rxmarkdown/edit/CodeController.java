/*
 * Copyright (C) 2016 yydcdut (yuyidong2015@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yydcdut.rxmarkdown.edit;

import android.text.Editable;

import com.yydcdut.rxmarkdown.factory.AbsGrammarFactory;
import com.yydcdut.rxmarkdown.grammar.IGrammar;
import com.yydcdut.rxmarkdown.grammar.edit.EditGrammarFacade;
import com.yydcdut.rxmarkdown.span.MDCodeSpan;

import java.util.List;

/**
 * RxMDEditText, code controller.
 * <p>
 * Created by yuyidong on 16/7/22.
 */
public class CodeController extends AbsEditController {

    private static final String KEY = "`";

    @Override
    public void beforeTextChanged(CharSequence s, int start, int before, int after) {
        super.beforeTextChanged(s, start, before, after);
        if (before == 0 || mRxMDConfiguration == null) {
            return;
        }
        String deleteString = s.subSequence(start, start + before).toString();
        String beforeString = null;
        String afterString = null;
        if (start > 0) {
            beforeString = s.subSequence(start - 1, start).toString();
        }
        if (start + before + 1 <= s.length()) {
            afterString = s.subSequence(start + before, start + before + 1).toString();
        }
        //`1``(``1`)(```1)(1```) --> ```
        if (deleteString.contains(KEY) || KEY.equals(beforeString) || KEY.equals(afterString)) {
            shouldFormat = true;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int after) {
        if (mRxMDConfiguration == null && !(s instanceof Editable)) {
            return;
        }
        if (shouldFormat) {
            format((Editable) s, start);
            return;
        }
        if (after == 0) {
            return;
        }
        String addString;
        String beforeString = null;
        String afterString = null;
        addString = s.subSequence(start, start + after).toString();
        if (start + 1 <= s.length()) {
            afterString = s.subSequence(start, start + 1).toString();
        }
        if (start > 0) {
            beforeString = s.subSequence(start - 1, start).toString();
        }
        //``` --> `1``(``1`)(```1)(1```)
        if (addString.contains(KEY) || KEY.equals(beforeString) || KEY.equals(afterString)) {
            format((Editable) s, start);
        }
    }

    private void format(Editable editable, int start) {
        EditUtils.removeSpans(editable, start, MDCodeSpan.class);
        IGrammar iGrammar = EditGrammarFacade.getAndroidGrammar(AbsGrammarFactory.GRAMMAR_CODE, mRxMDConfiguration);
        List<EditToken> editTokenList = iGrammar.format(editable);
        EditUtils.setCodeSpan(editable, editTokenList);
    }
}
