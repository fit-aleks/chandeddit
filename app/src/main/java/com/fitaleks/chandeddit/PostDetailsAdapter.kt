package com.fitaleks.chandeddit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.TextView
import com.fitaleks.chandeddit.data.RedditComment
import com.fitaleks.chandeddit.data.RedditPost
import com.fitaleks.chandeddit.util.CodeTagHandler
import com.fitaleks.chandeddit.util.timeDiffToStringShort
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_post_comment.*
import kotlinx.android.synthetic.main.item_post_text.*
import java.util.*


/**
 * Created by Alexander on 03.04.2018.
 */
class TextPostItem(private val redditPost: RedditPost?) : Item() {

    override fun getLayout(): Int = R.layout.item_post_text

    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
        if (redditPost == null) {
            return
        }
        if (!redditPost.title.isEmpty()) {
            viewHolder.item_post_details_title.text = redditPost.title
        }
        if (redditPost.selftextHtml == null || redditPost.selftextHtml.isEmpty()) {
            return
        }
        val codeTagHandler = CodeTagHandler()
        val mainTextUnescaped = redditPost.selftextHtml.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("\n\n", "<br/>")
                .replace("\n", "<br/>")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            viewHolder.item_post_details_text.text = Html.fromHtml(mainTextUnescaped)
        } else {
            viewHolder.item_post_details_text.text = Html.fromHtml(mainTextUnescaped, Html.FROM_HTML_MODE_COMPACT, null, codeTagHandler)
        }

        viewHolder.item_post_details_text.movementMethod = LinkMovementMethod.getInstance()
    }

}

class CommentHeaderItem : Item() {
    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
    }

    override fun getLayout(): Int = R.layout.item_post_comments_title
}

open class CommentItem(private val comment: RedditComment, val depth: Int = 0) : Item() {

    override fun getLayout(): Int = R.layout.item_post_comment

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val diff = Date().time - Date(comment.createdUtc.toLong() * 1000).time
        viewHolder.authorTextView.text = viewHolder.authorTextView.context.getString(R.string.item_reddit_comment_created, comment.author, timeDiffToStringShort(diff))
        // one convert from html to text is not enough. format requires it to be done twice

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolder.commentTextView.text = Html.fromHtml(Html.fromHtml(comment.bodyHtml, Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY)
        } else {
            viewHolder.commentTextView.text = Html.fromHtml(Html.fromHtml(comment.bodyHtml).toString())
        }
        viewHolder.commentTextView.movementMethod = LocalLinkMovementMethod


        buildDepth(viewHolder)
    }

    private fun buildDepth(viewHolder: ViewHolder) {
        viewHolder.depth_indicator_container.removeAllViews()
        val layoutInflater = LayoutInflater.from(viewHolder.itemView.context)
        (0 until depth).forEach {
            val separator = layoutInflater.inflate(R.layout.depth_indicator, viewHolder.depth_indicator_container, false)
            viewHolder.depth_indicator_container.addView(separator)
        }
        viewHolder.depth_indicator_container.requestLayout()
    }
}

class ExpandableComment(comment: RedditComment, depth: Int = 0) : CommentItem(comment, depth), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)

        viewHolder.itemView.setOnClickListener {
            expandableGroup.onToggleExpanded()
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }
}

class TextViewClickableWithLinks : TextView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var linkHit: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        linkHit = false
        val res = super.onTouchEvent(event)
        if (linkHit) {
            return res
        }
        return false
    }
}

object LocalLinkMovementMethod : LinkMovementMethod() {

    override fun onTouchEvent(widget: TextView,
                              buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val link = buffer.getSpans(
                    off, off, ClickableSpan::class.java)

            if (link.size != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget)
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]))
                }

                if (widget is TextViewClickableWithLinks) {
                    widget.linkHit = true
                }
                return true
            } else {
                Selection.removeSelection(buffer)
                Touch.onTouchEvent(widget, buffer, event)
                return false
            }
        }
        return Touch.onTouchEvent(widget, buffer, event)
    }

    /*companion object {
        internal var sInstance: LocalLinkMovementMethod? = null


        val instance: LocalLinkMovementMethod
            get() {
                if (sInstance == null)
                    sInstance = LocalLinkMovementMethod()

                return sInstance
            }
    }*/
}